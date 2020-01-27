package ru.hubsmc.hubscore;

public interface HubsServer {

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onPlayerJoin();

    void onPlayerQuit();

}
