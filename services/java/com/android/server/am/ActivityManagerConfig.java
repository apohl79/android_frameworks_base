/*
 * Copyright (C) 2012 Andreas Pohl, OSARMOD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.am;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Enumeration;
import java.util.HashMap;
import android.util.Slog;

public final class ActivityManagerConfig {
	private static final String CONFIG_FILE_NAME = "/system/etc/activitymanager.conf";
    private static final String TAG = "ActivityManagerConfig";

	// Return this value as default
	public static final int NO_MAX_OOM_ADJ = Integer.MAX_VALUE;

	// Map process names to the highest oom_adj value
	private HashMap<String, Integer> mProcNameMap = new HashMap<String, Integer>();

	public ActivityManagerConfig() {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(CONFIG_FILE_NAME));
			Enumeration e = p.propertyNames();
			while (e.hasMoreElements()) {
				String proc = (String) e.nextElement();
				Integer adj = Integer.valueOf(p.getProperty(proc));
				mProcNameMap.put(proc, adj);
				Slog.i(TAG, "Setting max oom_adj of " + adj + " for " + proc);
			}
		} catch (IOException e) {
			Slog.i(TAG, "No config loaded (" + e.getMessage() + ")");
		} catch (Exception e) {
			Slog.e(TAG, "Loading config failed: " + e.getMessage());
		}
	}

	public int getMaxOomAdj(String processName) {
		Integer adj = mProcNameMap.get(processName);
		return adj != null? adj.intValue(): NO_MAX_OOM_ADJ;
	}
}
