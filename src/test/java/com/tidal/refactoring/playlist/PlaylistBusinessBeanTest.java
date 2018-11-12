package com.tidal.refactoring.playlist;

import com.google.inject.Inject;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.testng.AssertJUnit.assertTrue;

@Guice(modules = TestBusinessModule.class)
public class PlaylistBusinessBeanTest {

    @Inject
    PlaylistBusinessBean playlistBusinessBean;

    @BeforeMethod
    public void setUp() throws Exception {

    }

    @AfterMethod
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddTracks() throws Exception {
        List<Track> trackList = new ArrayList<Track>();

        Track track = new Track();
        track.setArtistId(4);
        track.setTitle("A brand new track");
        track.setId(76868);

        trackList.add(track);

        List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(UUID.randomUUID().toString(), trackList, 5);

        assertTrue(playListTracks.size() > 0);
    }
    
    @Test
    public void testAddMultipleTracks() {
        List<Track> trackList = getTracks(5);
        List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(UUID.randomUUID().toString(), trackList, 5);
        assertTrue(playListTracks.size() >= 5);
    }
  
    @Test(expectedExceptions=PlaylistException.class)
    public void testAddLargeAmountsOfTracks() {
        List<Track> trackList = getTracks(600);
        List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(UUID.randomUUID().toString(), trackList, 5);
        assertTrue(playListTracks.size() >= 5);
    }
    
    private List<Track> getTracks(int count) {
        List<Track> trackList = new ArrayList<Track>();
    	for(int index =0;index<count;index++) {
            Track track = new Track();
            track.setArtistId(index);
            track.setTitle("A brand new track "+index);
            track.setId((int) (50000*Math.random()));
            trackList.add(track);
    	}
    	return trackList;
    }
       
    @Test
    public void testRemoveSingleTrack() {
        List<Track> trackList = getTracks(10);
        String uuid = UUID.randomUUID().toString();
        List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(uuid, trackList, 1);
        int playListSize = playListTracks.size();
		assertTrue(playListSize > 0);
		List<Integer> tracksTobeDeleted = Arrays.asList(3); 
        List<PlayListTrack> playListTracksAfterDeletion = playlistBusinessBean.removeTracks(uuid, tracksTobeDeleted);
		assertTrue(386-1 == playListTracksAfterDeletion.size());
    }
    
    @Test
	public void testRemoveMultipleTracks() {
		List<Track> trackList = getTracks(10);
		String uuid = UUID.randomUUID().toString();
		List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(uuid, trackList, 1);
		int playListSize = playListTracks.size();
		assertTrue(playListSize > 0);
		List<Integer> tracksTobeDeleted = Arrays.asList(3, 4, 5);
		List<PlayListTrack> playListTracksAfterDeletion = playlistBusinessBean.removeTracks(uuid, tracksTobeDeleted);
		assertTrue(386 - 3 == playListTracksAfterDeletion.size());
	}
    
    @Test(expectedExceptions=PlaylistException.class)
    public void testRemoveLargeAmountsOfTracks() {
		List<Track> trackList = getTracks(5);
		String uuid = UUID.randomUUID().toString();
		List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(uuid, trackList, 1);
		int playListSize = playListTracks.size();
		assertTrue(playListSize > 0);
		List<Integer> tracksTobeDeleted = new ArrayList<>();
		for (int index = 0; index < 510; index++) {
			tracksTobeDeleted.add(index);
		}
		playlistBusinessBean.removeTracks(uuid, tracksTobeDeleted);
    }
    
    @Test(expectedExceptions=PlaylistException.class)
    public void testRemoveTrackWhichDoesNotExist() {
		List<Track> trackList = getTracks(1);
		String uuid = UUID.randomUUID().toString();
		List<PlayListTrack> playListTracks = playlistBusinessBean.addTracks(uuid, trackList, 1);
		int playListSize = playListTracks.size();
		assertTrue(playListSize > 0);
		List<Integer> tracksTobeDeleted = Arrays.asList(500);
		playlistBusinessBean.removeTracks(uuid, tracksTobeDeleted);
    }

    
}