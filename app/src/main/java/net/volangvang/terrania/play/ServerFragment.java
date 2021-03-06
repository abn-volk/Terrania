package net.volangvang.terrania.play;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import net.volangvang.terrania.play.server.Server;

/**
 * A simple {@link Fragment} subclass.
 */
public class ServerFragment extends Fragment {

    private Server server;
    private String mode;
    private int score = 0;
    private String gameID;
    private String continent;
    private boolean local;

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public ServerFragment() {
        // Required empty public constructor
    }

    public void setServer (Server svr) {
        server = svr;
    }

    public Server getServer() {
        return server;
    }

    public String getMode() {
        return mode;
    }

    public int getScore() {
        return score;
    }

    public int bumpScore() {
        return ++score;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    public String getGameID() {
        return gameID;
    }

    public void setGameID(String id) {
        this.gameID = id;
    }
}
