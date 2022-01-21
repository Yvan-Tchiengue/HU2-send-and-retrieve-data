/*package pis.hue2.common;

import pis.hue2.client.LaunchClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientAusgabe implements Ausgabe {

    LaunchClient clien = new LaunchClient(new Socket(InetAddress.getLocalHost(), 2000)) ;

    public ClientAusgabe() throws IOException {
    }

    @Override
    public boolean isloggeding() {
        return clien.isIsloggedin();
    }

    @Override
    public byte[] readDat(long size) {
        return new byte[0];
    }

    @Override
    public String readLin() {
        return null;
    }

    @Override
    public boolean tryConnecte() {
        return false;
    }

    @Override
    public boolean disconnect() {
        return false;
    }

    @Override
    public String[] doListe() {
        return new String[0];
    }

    @Override
    public boolean putFil(String name) {
        return false;
    }

    @Override
    public boolean writeDat(long remainingBytes, InputStream stream) {
        return false;
    }

    @Override
    public boolean getFil(String name, OutputStream oStream) {
        return false;
    }

    @Override
    public boolean deleteFil(String name) {
        return false;
    }

    @Override
    public Thread createClien(String name) {
        return null;
    }
}*/