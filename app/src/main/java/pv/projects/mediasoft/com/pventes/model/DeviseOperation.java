package pv.projects.mediasoft.com.pventes.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by mediasoft on 01/08/2017.
 */

public class DeviseOperation  implements Parcelable{
    long id = 0 ;
    long operation_id = 0 ;
    long devise_id = 0 ;
    double montant = 0 ;

    public DeviseOperation() {

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOperation_id() {
        return operation_id;
    }

    public void setOperation_id(long operation_id) {
        this.operation_id = operation_id;
    }

    public long getDevise_id() {
        return devise_id;
    }

    public void setDevise_id(long devise_id) {
        this.devise_id = devise_id;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }


    public DeviseOperation(long id, long operation_id, long devise_id, double montant) {
        this.id = id;
        this.operation_id = operation_id;
        this.devise_id = devise_id;
        this.montant = montant;
    }

    public DeviseOperation(Parcel dest){
        id = dest.readLong() ;
        operation_id = dest.readLong() ;
        devise_id = dest.readLong() ;
        montant = dest.readDouble() ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(operation_id);
        dest.writeLong(devise_id);
        dest.writeDouble(montant);
    }



    public static final Creator<DeviseOperation> CREATOR = new Creator<DeviseOperation>() {
        @Override
        public DeviseOperation createFromParcel(Parcel in) {
            return new DeviseOperation(in);
        }

        @Override
        public DeviseOperation[] newArray(int size) {
            return new DeviseOperation[size];
        }
    };
}
