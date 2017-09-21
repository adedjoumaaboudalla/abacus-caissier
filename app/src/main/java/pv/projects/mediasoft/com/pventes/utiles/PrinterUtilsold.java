package pv.projects.mediasoft.com.pventes.utiles;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.zj.btsdk.BluetoothService;
import com.zj.btsdk.PrintPic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import pv.projects.mediasoft.com.pventes.R;
import pv.projects.mediasoft.com.pventes.dao.CompteBanqueDAO;
import pv.projects.mediasoft.com.pventes.dao.DeviseDAO;
import pv.projects.mediasoft.com.pventes.dao.DeviseOperationDAO;
import pv.projects.mediasoft.com.pventes.dao.MouvementDAO;
import pv.projects.mediasoft.com.pventes.dao.OperationDAO;
import pv.projects.mediasoft.com.pventes.dao.PartenaireDAO;
import pv.projects.mediasoft.com.pventes.dao.PointVenteDAO;
import pv.projects.mediasoft.com.pventes.model.CompteBanque;
import pv.projects.mediasoft.com.pventes.model.Devise;
import pv.projects.mediasoft.com.pventes.model.DeviseOperation;
import pv.projects.mediasoft.com.pventes.model.Mouvement;
import pv.projects.mediasoft.com.pventes.model.Operation;
import pv.projects.mediasoft.com.pventes.model.Partenaire;
import pv.projects.mediasoft.com.pventes.model.PointVente;


public class PrinterUtilsold {

    private final String bluetoothConfig = "bluetoothConfig";
    private final String serverIp = "DP-HT201";
    private String matriculeCollecteur = null;
    private String msgFin = null;
    private String societeNom = null;
    private String societeAdresse = null;
    // android built in classes for bluetooth operations
			BluetoothAdapter mBluetoothAdapter;
			BluetoothSocket mmSocket;
			BluetoothDevice mmDevice;
            public static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy H:m:s") ;
			
			OutputStream mmOutputStream;
			InputStream mmInputStream;
			Thread workerThread;
			
			byte[] readBuffer;
			int readBufferPosition;
			int counter;
			volatile boolean stopWorker;
			
			String msg;
			String nomImprimante;
            MouvementDAO mouvementDAO = null ;
            DeviseDAO deviseDAO = null ;
            DeviseOperationDAO deviseOperationDAO = null ;
            OperationDAO operationDAO = null ;
            PointVenteDAO pointVenteDAO = null ;
            Context context = null ;
            BluetoothService mService ;

			public PrinterUtilsold(Context context, BluetoothService mService){
                SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
                nomImprimante = preferences.getString(bluetoothConfig, serverIp) ;
                pointVenteDAO = new PointVenteDAO(context) ;
                mouvementDAO = new MouvementDAO(context) ;
                operationDAO = new OperationDAO(context) ;
                deviseDAO = new DeviseDAO(context) ;
                deviseOperationDAO = new DeviseOperationDAO(context) ;
                this.context = context ;
                societeNom = preferences.getString("societeNom", pointVenteDAO.getLast().getLibelle()) ;
                societeAdresse = context.getString(R.string.tel) + pointVenteDAO.getLast().getTel() + context.getString(R.string.adresse) + pointVenteDAO.getLast().getVille()+ ", "+ pointVenteDAO.getLast().getPays();
                msgFin = preferences.getString("messagefinal", context.getString(R.string.gooddays)) ;

                this.mService = mService ;
            }


			
			public PrinterUtilsold(String msg){
				this.msg=msg;
			}
			
			public PrinterUtilsold(String msg, String nomImprimante){
				this.msg = msg;
				if(nomImprimante.equals("")){
					this.nomImprimante = "BlueTooth Printer";
				}else{
				this.nomImprimante = nomImprimante;
				}
				Log.d("PRinter ", "Imprimante :" + nomImprimante);
			}

	/*
	 * This will find a bluetooth printer device
	 */
	void findBT() {

		try {
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

			if (mBluetoothAdapter == null) {
				//myLabel.setText("No bluetooth adapter available");
			}

			if (!mBluetoothAdapter.isEnabled()) {
//				Intent enableBluetooth = new Intent(
//						BluetoothAdapter.ACTION_REQUEST_ENABLE);
//				startActivityForResult(enableBluetooth, 0);
			}

			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
					.getBondedDevices();
			if (pairedDevices.size() > 0) {
				for (BluetoothDevice device : pairedDevices) {
					
					// MP300 is the name of the bluetooth printer device
//					if (device.getName().equals("BlueTooth Printer")) {
					if (device.getName().equals(nomImprimante)) {
						mmDevice = device;
						Log.d("Printer","Bluetooth Device Found "+ device.getName());

						break;
					}
				}
			}
			
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Tries to open a connection to the bluetooth printer device
	 */
	void openBT() throws IOException {
		try {
			// Standard SerialPortService ID
			UUID uuid = UUID.fromString("00001105-0000-mille-8000-00805F9B34FB");
			mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
			mmSocket.connect();
			mmOutputStream = mmSocket.getOutputStream();
			mmInputStream = mmSocket.getInputStream();

			beginListenForData();
			Log.d("Printer","Bluetooth Device opened");
			//myLabel.setText("Bluetooth Opened");
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	private boolean _Open() {

		boolean valid = false;

		Method m;
		try {
			m = mmDevice.getClass().getMethod("createRfcommSocket",new Class[] { int.class });
			try {
				mmSocket = (BluetoothSocket) m.invoke(mmDevice, 1);
				mBluetoothAdapter.cancelDiscovery();
				
				try {
					mmSocket.connect();
					mmOutputStream = new DataOutputStream(mmSocket.getOutputStream());
					mmInputStream = new DataInputStream(mmSocket.getInputStream());
					valid = true;
					beginListenForData();
					Log.d("BTRWThread Open", "Connected to " + mmDevice.getName());
					valid = true;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return valid;
	}

	
	/*
	 * After opening a connection to bluetooth printer device, 
	 * we have to listen and check if a data were sent to be printed.
	 */
	void beginListenForData() {
		try {
			final Handler handler = new Handler();
			
			// This is the ASCII code for a newline character
			final byte delimiter = 10;

			stopWorker = false;
			readBufferPosition = 0;
			readBuffer = new byte[1024];
			
			workerThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (!Thread.currentThread().isInterrupted()
							&& !stopWorker) {
						
						try {
							
							int bytesAvailable = mmInputStream.available();
							if (bytesAvailable > 0) {
								byte[] packetBytes = new byte[bytesAvailable];
								mmInputStream.read(packetBytes);
								for (int i = 0; i < bytesAvailable; i++) {
									byte b = packetBytes[i];
									if (b == delimiter) {
										byte[] encodedBytes = new byte[readBufferPosition];
										System.arraycopy(readBuffer, 0,
												encodedBytes, 0,
												encodedBytes.length);
										final String data = new String(
//												encodedBytes, "US-ASCII");
												encodedBytes, "UTF-8");
										readBufferPosition = 0;

										handler.post(new Runnable() {
											@Override
											public void run() {
												//myLabel.setText(data);
											}
										});
									} else {
										readBuffer[readBufferPosition++] = b;
									}
								}
							}
							
						} catch (IOException ex) {
							stopWorker = true;
						}
						
					}
				}
			});

			workerThread.start();
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * This will send data to be printed by the bluetooth printer
	 */
	void sendData() throws IOException {
		try {
			mmOutputStream.write(msg.getBytes());
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Close the connection to bluetooth printer.
	 */
	void closeBT() throws IOException {
		try {
			stopWorker = true;
			mmOutputStream.close();
			mmInputStream.close();
			mmSocket.close();
			//myLabel.setText("Bluetooth Closed");
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Imprimer
	 */
	
	public void print(){

		try {
			findBT();
			_Open();	
			sendData();
			closeBT();
		} catch (IOException ex) {
		}
	}


	public void printTicket(long id,int duplicata){
		Operation operation = operationDAO.getOne(id) ;
		if (operation==null) return;
		Partenaire partenaire = new PartenaireDAO(context).getOne(operation.getPartenaire_id()) ;
		CompteBanque compteBanque = new CompteBanqueDAO(context).getOne(operation.getComptebanque_id()) ;

        ArrayList<Mouvement> mouvements = mouvementDAO.getMany(operation.getId()) ;
        Devise reference = deviseDAO.getReference() ;
        Calendar cal = Calendar.getInstance() ;

        msg = "##############################" ;
        msg+= "\n";
        msg +=  aligntCenter(societeNom) ;
        msg+= "\n";
        msg+= context.getString(R.string.telephone) + pointVenteDAO.getLast().getTel();
        msg+= "\n";
        if (operation.getAttente()==0) msg += "################################" ;
        else msg +=                           "############# DEVIS ############" ;
        msg+= "\n";
        msg+= "Date      : "+ formatter.format(new Date());
        msg+= "\n";
        msg+= "Ticket No : "+ operation.getId()+ "/" + cal.get(Calendar.YEAR);
        msg+= "\n";
        if (partenaire==null)msg+= "Client    : "+ operation.getClient();
		else msg+= "Client    : "+ operation.getClient() + "("  + partenaire.getContact() + ")";
        msg+= "\n";
        msg += "--------------------------------";
        msg += "\n";
        msg+= context.getString(R.string.enteteputil);
        msg += "\n";
        msg += "--------------------------------";
        msg+= "\n";
        int n = mouvements.size() ;
        for (int i = 0; i < n; i++){
			Mouvement mv = mouvements.get(i) ;
			String ligne = String.valueOf(mv.getQuantite()) + " " + Utiles.formatMtn(mv.getPrixV())   + " " + Utiles.formatMtn(mv.getPrixV()*mv.getQuantite())  ;
			msg+= mv.getProduit() + " \n" ;
			msg += alignRight(ligne) ;
			msg+= "\n";
        }
        msg += "--------------------------------";
        msg+= "\n";
        msg+= context.getString(R.string.total);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getMontant()) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getMontant())) ;
        msg+= "\n";
        msg+= context.getString(R.string.nbrearticle) ;
        msg+= totaux(Utiles.formatMtn(mouvements.size()));
        msg+= "\n";
        msg+= context.getString(R.string.print_remise);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getRemise()) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getRemise())) ;
        msg+= "\n";
        msg+= context.getString(R.string.print_net_a_payer);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getMontant()-operation.getRemise()) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getMontant()-operation.getRemise())) ;
        msg+= "\n";
        msg += "--------------------------------";
        msg+= "\n";
        msg+= context.getString(R.string.print_recu);
        msg+= "\n";
        ArrayList<DeviseOperation> deviseOps = deviseOperationDAO.getMany(operation.getId_externe());
        n = deviseOps.size() ;
        for (int i = 0; i < n; i++){
            Devise devise = deviseDAO.getOne(deviseOps.get(i).getDevise_id()) ;
            String ligne = Utiles.formatMtn(deviseOps.get(i).getMontant()) + " " +  devise.getCodedevise()  ;
            msg += alignRight1(ligne) ;
            msg+= "\n";
        }
        if (n==0) msg += alignRight1(String.valueOf(operation.getRecu())) ;
		msg+= "\n";
		msg+= context.getString(R.string.print_mode);
		msg+= operation.getModepayement();
		msg+= "\n";
		if (compteBanque!=null){
            msg+= context.getString(R.string.print_bk) + compteBanque.getLibelle();
            msg+= "\n";
        }
		msg += "--------------------------------";
        msg+= "\n";
        msg+= context.getString(R.string.print_rendu);
        if (reference!=null){
            msg+= totaux1(Utiles.formatMtn(operation.getRecu()-(operation.getMontant()-operation.getRemise())) + reference.getCodedevise()) ;
        }
        else msg+= totaux(Utiles.formatMtn(operation.getRecu()-(operation.getMontant()-operation.getRemise()))) ;
        msg+= "\n";

		if (operation.getDescription().length()>0){
			msg += "--------------------------------";
			msg += "\n";
			msg += operation.getDescription() ;
			msg += "\n";
		}

        msg += "--------------------------------";
        msg += "\n";
        msg += msgFin;
        msg += "\n";
        msg += "################################";
        msg += "\n";
        msg += context.getString(R.string.copyright);
        msg += "\n";
        msg += "\n";
        msg += "\n";
        msg += "\n";

		new Thread(
                new Runnable(){

            @Override
            public void run() {
                try {
                    print();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        }).start();
    }

	private String alignRight(String ligne) {
		String result = "" ;
		for (int i = 0; i < 33 - ligne.length(); i++) {
			result += " " ;
		}
		result = result + ligne ;
		return result;
	}

	private String alignRight1(String ligne) {
		String result = "" ;
		for (int i = 0; i < 32 - ligne.length(); i++) {
			result += " " ;
		}
		result = result + ligne ;
		return result ;
	}

	private String aligntCenter(String ligne) {
		String result = "" ;
        if (ligne.length()>=30) return ligne ;
		for (int i = 0; i < 30 - ligne.length(); i++) {
			if (i==(30-ligne.length())/2)result += ligne ;
            else result += " " ;
		}
		return result;
	}


    public String name(String name){
        int n = 11 ;
        int siz = 0 ;
        Log.e("NAME1",name) ;
        name = (name.length()>6)?(name.substring(0,6)+".."):(name) ;
        siz = n-name.length() ;
        for (int i =0 ; i < siz; ++i){
            //name += " " ;
        }
        Log.e("NAME2",name) ;
        return name ;
    }

    public String depenselibelle(String name){
        int n = 17 ;
        int siz = 0 ;
        Log.e("NAME1",name) ;
        name = (name.length()>17)?(name.substring(0,16)):(name) ;
        siz = n-name.length() ;
        for (int i =0 ; i < siz; ++i){
            name += " " ;
        }
        Log.e("NAME2",name) ;
        return name ;
    }

    public String prix(String prix){
        int n = 6 ;
        int siz = 0 ;
        prix = (prix.length()>6)?(prix.substring(0,5)):(prix) ;
        siz = n-prix.length() ;
        for (int i =0 ; i < siz; ++i){
            prix = " " + prix ;
        }
        return prix ;
    }

    public String montant(String prix){
        int n = 6 ;
        int siz = 0 ;
        prix = (prix.length()>6)?(prix.substring(0,5)):(prix) ;
        siz = n-prix.length() ;
        for (int i =0 ; i < siz; ++i){
            prix = " " + prix ;
        }
        return prix ;
    }

    public String quantite(String prix){
        int n = 5 ;
        int siz = 0 ;
        prix = (prix.length()>5)?(prix.substring(0,4)):(prix) ;
        siz = n-prix.length() ;
        for (int i =0 ; i < siz; ++i){
            prix = " " + prix ;
        }
        return prix ;
    }

    public String totaux(String totaux){
        int n = 22 ;
        int siz = 0 ;
        totaux = (totaux.length()>21)?(totaux.substring(0,20)):(totaux) ;
        siz = n-totaux.length() ;
        for (int i =0 ; i < siz; ++i){
            totaux = " " + totaux ;
        }
        return totaux ;
    }

    public String totaux1(String totaux){
        int n = 20 ;
        int siz = 0 ;
        totaux = (totaux.length()>18)?(totaux.substring(0,17)):(totaux) ;
        siz = n-totaux.length() ;
        for (int i =0 ; i < siz; ++i){
            totaux = " " + totaux ;
        }
        return totaux ;
    }


    public void print(String msg) {
        this.msg = msg ;
        try {
            print();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }


}
