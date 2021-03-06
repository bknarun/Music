package com.tidal.refactoring.playlist;

import com.google.inject.Inject; 
import com.tidal.refactoring.playlist.dao.PlaylistDaoBean;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.data.PlayList;
import com.tidal.refactoring.playlist.exception.PlaylistException;

import java.util.*;

public class PlaylistBusinessBean {

    private PlaylistDaoBean playlistDaoBean;

    @Inject
    public PlaylistBusinessBean(PlaylistDaoBean playlistDaoBean){
        this.playlistDaoBean = playlistDaoBean;
    }

    /**
     * Add tracks to the index
     */
    List<PlayListTrack> addTracks(String uuid, List<Track> tracksToAdd, int toIndex) throws PlaylistException {

        try {

            PlayList playList = playlistDaoBean.getPlaylistByUUID(uuid);
            
            if(tracksToAdd==null || tracksToAdd.size()==0) {
                throw new PlaylistException("Track List cannot be empty or have atleast on track to add");	
            }

            //We do not allow > 500 tracks in new playlists
            if (playList.getNrOfTracks() + tracksToAdd.size() > 500) {
                throw new PlaylistException("Playlist cannot have more than " + 500 + " tracks");
            }

            // The index is out of bounds, put it in the end of the list.
            int size = playList.getPlayListTracks() == null ? 0 : playList.getPlayListTracks().size();
            if (toIndex > size || toIndex == -1) {
                toIndex = size;
            }

            if (!validateIndexes(toIndex, playList.getNrOfTracks())) {
                return Collections.EMPTY_LIST;
            }

            Set<PlayListTrack> originalSet = playList.getPlayListTracks();
            List<PlayListTrack> original;
            if (originalSet == null || originalSet.size() == 0)
                original = new ArrayList<PlayListTrack>();
            else
                original = new ArrayList<PlayListTrack>(originalSet);

            Collections.sort(original);

            List<PlayListTrack> added = new ArrayList<PlayListTrack>(tracksToAdd.size());

            for (Track track : tracksToAdd) {
                PlayListTrack playlistTrack = new PlayListTrack();
                playlistTrack.setTrack(track);
                playlistTrack.setTrackPlaylist(playList);
                playlistTrack.setDateAdded(new Date());
                playlistTrack.setTrack(track);
                playList.setDuration(addTrackDurationToPlaylist(playList, track));
                original.add(toIndex, playlistTrack);
                added.add(playlistTrack);
                toIndex++;
            }

            int i = 0;
            for (PlayListTrack track : original) {
                track.setIndex(i++);
            }

            playList.getPlayListTracks().clear();
            playList.getPlayListTracks().addAll(original);
            playList.setNrOfTracks(original.size());
            
            playlistDaoBean.updatePlayList(uuid, playList);

            return added;

        } catch (Exception e) {
            e.printStackTrace();
            throw new PlaylistException("Generic error");
        }
    }
    
	/**
	 * Remove the tracks from the playlist located at the sent indexes
	 */
	List<PlayListTrack> removeTracks(String uuid, List<Integer> indexes) throws PlaylistException {
		try {
			PlayList playList = playlistDaoBean.getPlaylistByUUID(uuid);     
			
            indexes = validateAndRemoveDuplicateIndex(indexes);

			if (indexes.size() > 500 || indexes.get(indexes.size() - 1) > 500) {
				throw new PlaylistException("Playlist cannot have more than " + 500 + " tracks");
			}

            List<PlayListTrack> playListTracks = new ArrayList<PlayListTrack>(playList.getPlayListTracks());

            Collections.sort(playListTracks);

			if (playListTracks.size() < indexes.size()
					|| playListTracks.get(playListTracks.size() - 1).getIndex() < indexes.get(indexes.size() - 1)) {
				throw new PlaylistException("Playlist cannot have more than " + indexes.size() + " tracks");
			}
			         
            List<PlayListTrack> removeableList = new ArrayList<>();
            for(PlayListTrack eachTrack: playListTracks) {
            	if(eachTrack.getIndex()==indexes.get(0)) {
            		removeableList.add(eachTrack);
            		indexes.remove(0);
            	}
            	if(indexes.size()==0) {
            		break;
            	}
            }
            
            playListTracks.removeAll(removeableList);
            
            int i = 0;
            for (PlayListTrack track : playListTracks) {
                track.setIndex(i++);
            }

            playList.getPlayListTracks().clear();
            playList.getPlayListTracks().addAll(playListTracks);
            playList.setNrOfTracks(playListTracks.size());
            
            playlistDaoBean.updatePlayList(uuid, playList);
            
			return playListTracks;
		} catch (Exception e) {
			e.printStackTrace();
			throw new PlaylistException("Generic error");
		}
	}

	/**
	 * Remove duplicate index from list and return sorted list
	 */
	private List<Integer> validateAndRemoveDuplicateIndex(List<Integer> indexes) {
		Set<Integer> number =  new HashSet<>(indexes);
		indexes = new ArrayList<Integer>(number);
		Collections.sort(indexes);
		return indexes;
	}
	
	private boolean validateIndexes(int toIndex, int length) {
        return toIndex >= 0 && toIndex <= length;
    }

    private float addTrackDurationToPlaylist(PlayList playList, Track track) {
        return (track != null ? track.getDuration() : 0)
                + (playList != null && playList.getDuration() != null ? playList.getDuration() : 0);
    }
}
