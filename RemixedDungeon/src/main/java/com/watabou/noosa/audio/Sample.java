/*
 * Copyright (C) 2012-2014  Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.noosa.audio;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import androidx.annotation.NonNull;

import com.nyrds.android.util.ModdingMode;
import com.nyrds.pixeldungeon.ml.EventCollector;
import com.watabou.noosa.Game;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Sample implements SoundPool.OnLoadCompleteListener {

	INSTANCE;

	public static final int MAX_STREAMS = 8;
	String playOnComplete;

	protected SoundPool pool =
			new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);

	@NonNull
	protected Set<String> missingAssets = new HashSet<>();

	@NotNull
	protected Map<String, Integer> ids =
			new HashMap<>();

	private AssetManager manager;
	private boolean enabled = true;

	public void reset() {

		pool.release();

		pool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
		pool.setOnLoadCompleteListener(this);

		ids.clear();
	}

	public void pause() {
		if (pool != null) {
			pool.autoPause();
		}
	}

	public void resume() {
		if (pool != null) {
			pool.setOnLoadCompleteListener(this);
			if (manager == null) {
				manager = Game.instance().getAssets();
			}
			pool.autoResume();
		}
	}

	private void load(String asset) {

		if (!ids.containsKey(asset) && !missingAssets.contains(asset)) {
			try {
				String assetFile = "sound/" + asset;
				int streamID;

				File file = ModdingMode.getFile(assetFile);
				if (file != null && file.exists()) {
					streamID = pool.load(file.getAbsolutePath(), 1);
				} else {
					streamID = fromAsset(manager, assetFile);
				}

				ids.put(asset, streamID);

			} catch (IOException e) {
				missingAssets.add(asset);
				playOnComplete = null;
				EventCollector.logException(e,asset);
			}
		}
	}

	private int fromAsset(AssetManager manager, String asset)
			throws IOException {
		AssetFileDescriptor fd = manager.openFd(asset);
		int streamID = pool.load(fd, 1);
		fd.close();
		return streamID;
	}

	public int play(String id) {
		return play(id, 1, 1, 1);
	}

	public int play(String id, float volume) {
		return play(id, volume, volume, 1);
	}

	public int play(String id, float leftVolume, float rightVolume, float rate) {
		if(!enabled) {
			return -1;
		}

		if (ids.containsKey(id)) {
			return pool.play(ids.get(id), leftVolume, rightVolume, 0, 0, rate);
		} else {
			playOnComplete = id;
			load(id);
			return -1;
		}
	}

	public void enable(boolean value) {
		enabled = value;
	}

	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		if (status == 0 && playOnComplete != null) {
			play(playOnComplete);
			playOnComplete = null;
		}
	}
}
