package com.jms;
import com.jms.PlaylistItem;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.util.LinkedList;

/**
 * Created by jakub on 16.11.15.
 */
public class Playlist extends AbstractListModel<PlaylistItem> {

    private LinkedList<PlaylistItem> playlist;
    private ListDataListener listener;

    Playlist(){
        playlist = new LinkedList<>();
    }

    /**
     * Adds item at the end of playlist
     * @param item Item to add
     */
    public void addPlaylistItem(PlaylistItem item){
        playlist.add(item);
    }

    /**
     * Add given item to playlist at given position
     * @param item Item to be added
     * @param index Destination
     * @return false if index negative or out of bounds, otherwise true.
     */
    public boolean addPlaylistItem(PlaylistItem item, int index){
        if(index >= 0 && index < playlist.size()){
            playlist.add(index, item);
            fireIntervalAdded(item, index, index);
            return true;
        } else {
            return false;
        }
    }
    public PlaylistItem removePlaylistItem(int index){
        if(index >= 0 && index <playlist.size()){
            PlaylistItem tmp = playlist.remove(index);
            fireContentsChanged(tmp, index, index);
            return tmp;
        }
        else return null;
    }

//    /**
//     * Finds given item and moves it to new position
//     * @param item Item to be replaced
//     * @param index Destination index
//     * @return false if playlist doesn't contain item or index is out of bounds
//     */
//    public boolean replaceItem(PlaylistItem item, int index){
//        if(playlist.contains(item)){
//            playlist.remove(item);
//            playlist.add(index, item);
//            fireContentsChanged(item, index, index);
//            return true;
//        }
//        else return false;
//    }

    /**
     * Moves item from one index to another
     * @param itemIndex Source index
     * @param destination Destination index
     * @return false if one of indices is out of bounds
     */
    public boolean replaceItem(int itemIndex, int destination){
        if(itemIndex < playlist.size()){
            PlaylistItem temp = playlist.remove(itemIndex);
            playlist.add(destination, temp);
            if (itemIndex>destination)
                fireIntervalAdded(temp, destination, itemIndex);
            else
                fireIntervalAdded(temp,itemIndex, destination);
            return true;
        }
        else return false;
    }

    @Override
    public int getSize() {
        return playlist.size();
    }

    @Override
    public PlaylistItem getElementAt(int index) {
        return playlist.get(index);
    }

//    @Override
//    public void addListDataListener(ListDataListener l) {
//
//        listener = l;
//    }
//
//    @Override
//    public void removeListDataListener(ListDataListener l) {
//        l = null;
//    }
}
