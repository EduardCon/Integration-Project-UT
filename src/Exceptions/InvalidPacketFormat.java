package Exceptions;

import org.omg.CORBA.DynAnyPackage.Invalid;

public class InvalidPacketFormat extends Exception {

    public InvalidPacketFormat(String message) {
        super(message);
    }

}
