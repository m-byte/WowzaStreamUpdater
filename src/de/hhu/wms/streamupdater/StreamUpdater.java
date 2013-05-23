/**
 * Copyright (c) 2012, Heinrich-Heine-Universität Düsseldorf
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION
 * OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN
 * CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 * 
 * @author Matthias Breithaupt <matthias.breithaupt@uni-duesseldorf.de>
 * @version 1.0
 */

package de.hhu.wms.streamupdater;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.wowza.wms.amf.AMFPacket;
import com.wowza.wms.application.IApplication;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.application.WMSProperties;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.IMediaStreamActionNotify;
import com.wowza.wms.stream.IMediaStreamActionNotify2;
import com.wowza.wms.vhost.IVHost;
import com.wowza.wms.vhost.VHostSingleton;

import de.hhu.wms.streamupdater.hosttypes.app;
import de.hhu.wms.streamupdater.hosttypes.appinst;
import de.hhu.wms.streamupdater.hosttypes.host;
import de.hhu.wms.streamupdater.hosttypes.stream;

public class StreamUpdater extends ModuleBase
{
	public static LinkedList<host> hosts = new LinkedList<host>();

	/**
	 * Searches for a host object in <code>hosts</code>. If it cannot be found,
	 * returns <code>null</code>.
	 * <p>
	 * This function should NOT be modified when changing the update mechanism.
	 * 
	 * @param name the name of the host
	 * @return     the host object referenced by <code>name</code>
	 */
	public host getHost(String name) throws UnsupportedEncodingException{
		for(host currhost:hosts){
			if(currhost.getName().equals(URLEncoder.encode(name, "UTF-8")))
				return currhost;
		}
		return null;
	}

	/**
	 * Sends a status update notification. Gets called when a stream is started.
	 * <p>
	 * This function should be modified when changing the update mechanism.
	 * 
	 * @param host    the name of the host
	 * @param app     the name of the app
	 * @param appInst the name of the application instance
	 * @param stream  the name of the stream
	 * @param strurl  the URL of the stream
	 */
	public void streamStarted(String host, String app, String appInst, String stream, String strurl){
		try {
			String data = URLEncoder.encode("host", "UTF-8") + "=" + host;
			data += "&" + URLEncoder.encode("app", "UTF-8") + "=" + app;
			data += "&" + URLEncoder.encode("appinst", "UTF-8") + "=" + appInst;
			data += "&" + URLEncoder.encode("stream", "UTF-8") + "=" + stream;
			data += "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode("started", "UTF-8");
			URL url = new URL(strurl);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
			conn.getInputStream().read(); /* make sure the request has been sent */
		} catch(Exception e) {
		}
		// TODO: retry if failed
		//System.out.println("STREAMUPDATER! Send to " + strurl + ": " + host + " - " +  app + " - " +  appInst + " - " +  stream);
	}

	/**
	 * Sends a status update notification. Gets called when a stream is stopped.
	 * <p>
	 * This function should be modified when changing the update mechanism.
	 * 
	 * @param host    the name of the host
	 * @param app     the name of the app
	 * @param appInst the name of the application instance
	 * @param stream  the name of the stream
	 * @param strurl  the URL of the stream
	 */
	public void streamStopped(String host, String app, String appInst, String stream, String strurl){
		try {
			String data = URLEncoder.encode("host", "UTF-8") + "=" + host;
			data += "&" + URLEncoder.encode("app", "UTF-8") + "=" + app;
			data += "&" + URLEncoder.encode("appinst", "UTF-8") + "=" + appInst;
			data += "&" + URLEncoder.encode("stream", "UTF-8") + "=" + stream;
			data += "&" + URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode("stopped", "UTF-8");
			URL url = new URL(strurl);
			System.out.println(url.getPort());
			System.out.println(strurl);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();
			conn.getInputStream().read(); /* make sure the request has been sent */
		} catch(Exception e) {
		}
		// TODO: retry if failed
		//System.out.println("STREAMUPDATER! Send to " + url + ": " + host + " - " +  app + " - " +  appInst + " - " +  stream);
	}

	/**
	 * Gets called on every Wowza onStreamCreate event. It attaches a listener
	 * to the newly created Wowza stream.
	 * <p>
	 * This function should NOT be modified when changing the update mechanism.
	 * 
	 * @param stream Wowza {@link com.wowza.wms.stream.IMediaStream IMediaStream}
	 * 			     object that has been updated.
	 */
	@SuppressWarnings("unchecked")
	public void onStreamCreate(IMediaStream stream) {
		IMediaStreamActionNotify actionNotify  = new StreamListener();

		WMSProperties props = stream.getProperties();
		synchronized(props)
		{
			props.put("streamActionNotifier", actionNotify);
		}
		stream.addClientListener(actionNotify);
	}

	/**
	 * Gets called on every Wowza onStreamDestroy event. It detaches the
	 * listener created in {@link #onStreamCreate(IMediaStream) onStreamCreate}
	 * from the Wowza stream.
	 * <p>
	 * This function should NOT be modified when changing the update mechanism.
	 * 
	 * @param stream Wowza {@link com.wowza.wms.stream.IMediaStream IMediaStream}
	 * 				 object that has been updated.
	 */
	public void onStreamDestroy(IMediaStream stream) {
		IMediaStreamActionNotify actionNotify = null;
		WMSProperties props = stream.getProperties();
		synchronized(props)
		{
			actionNotify = (IMediaStreamActionNotify)stream.getProperties().get("streamActionNotifier");
		}
		if (actionNotify != null)
		{
			stream.removeClientListener(actionNotify);
		}
	}

	class StreamListener implements IMediaStreamActionNotify2 
	{
		/**
		 * Do NOT remove
		 */
		public void onPlay(IMediaStream stream, String streamName, double playStart, double playLen, int playReset) {
		}
		
		/**
		 * Do NOT remove
		 */
		public void onPauseRaw(IMediaStream stream, boolean isPause, double location) {
		}
		
		/**
		 * Do NOT remove
		 */
		public void onSeek(IMediaStream stream, double location){
		}
		
		/**
		 * Do NOT remove
		 */
		public void onStop(IMediaStream stream){
		}
		
		/**
		 * Called on every onUnPublish event. Searches for the proper stream
		 * object. If found, it removes the object and calls the {@link
		 * StreamUpdater#streamStopped(String, String, String, String, String)
		 * streamStopped} function.
		 */
		public void onUnPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend)
		{
			try {
				// create variables for strings that are used multiple times
				String appinstname = stream.getStreams().getAppInstanceName();
				String appname = stream.getStreams().getAppName();
				String hostname = stream.getStreams().getVHost().getName();
				// search for the stream object
				if(appinstname != null && appname != null && hostname != null && streamName != null){
					if(appinstname != null && appname != null && hostname != null && streamName != null){
						host currhost = getHost(hostname);
						if(currhost != null){
							app currapp = currhost.getApp(appname);
							if(currapp != null){
								appinst currappinst = currapp.getAppInst(appinstname);
								if(currappinst != null){
									stream currstream = currappinst.getStream(streamName);
									if(currstream!= null){
										// remove the stream object
										currappinst.streams.remove(currstream);
										// call the streamStopped function
										streamStopped(hostname, appname, appinstname, streamName, currapp.getServerUrl());
									}
								}
							}
						}
					}
				}
			} catch(Exception e) {

			}
		}

		/**
		 * Called on every onPublish event. Searches for the proper stream
		 * object. If found, it removes the object and calls the {@link
		 * StreamUpdater#streamStarted(String, String, String, String, String)
		 * streamStarted} function.
		 */
		@SuppressWarnings("unchecked")
		public  void onPublish(IMediaStream stream, String currStreamName, boolean isRecord, boolean isAppend)
		{
			try {
				if(hosts.isEmpty()) {
					List<String> vhostNames = VHostSingleton.getVHostNames();
					Iterator<String> iter = vhostNames.iterator();
					while (iter.hasNext()) {
						String vhostName = iter.next();
						IVHost vhost = VHostSingleton.getInstance(vhostName);
						if (vhost != null) {
							host currhost = new host(vhostName);
							List<String> appNames = vhost.getApplicationNames();
							Iterator<String> appNameIterator = appNames.iterator();
							while (appNameIterator.hasNext()) {
								String applicationName = appNameIterator.next();
								IApplication application = vhost.getApplication(applicationName);
								if (application == null)
									continue;
								app currapp = new app(applicationName, application.getConfigPath());
								if(currapp.isUpdaterEnabled()){
									List<String> appInstances = application.getAppInstanceNames();
									Iterator<String> iterAppInstances = appInstances.iterator();
									while (iterAppInstances.hasNext()) {
										String appInstanceName = iterAppInstances.next();
										IApplicationInstance appInst = application.getAppInstance(appInstanceName);
										if (appInst == null)
											continue;
										appinst currappinst = new appinst(appInstanceName);
										List<String> publishStreams = appInst.getStreams().getPublishStreamNames();

										Set<String> streamNames = new HashSet<String>();
										streamNames.addAll(publishStreams);

										Iterator<String> siter = streamNames.iterator();
										while(siter.hasNext()) {
											String streamName = siter.next();
											stream currstream = new stream(streamName);
											currappinst.streams.add(currstream);
											streamStarted(currhost.getName(), currapp.getName(), currappinst.getName(), currstream.getName(), currapp.getServerUrl());
										}
										currapp.appinsts.add(currappinst);
									}
								} else {
									onStreamDestroy(stream);
								}
								currhost.apps.add(currapp);
							}
							hosts.add(currhost);
						}
					}
				} else {
					String appinstname = stream.getStreams().getAppInstanceName();
					String appname = stream.getStreams().getAppName();
					String hostname = stream.getStreams().getVHost().getName();
					if(appinstname != null && appname != null && hostname != null && currStreamName != null){
						host currhost = getHost(hostname);
						if(currhost != null){
							app currapp = currhost.getApp(appname);
							if(currapp != null){
								if(currapp.isUpdaterEnabled()){
									appinst currappinst = currapp.getAppInst(appinstname);
									if(currappinst != null){
										if(currappinst.getStream(currStreamName)!= null){
											onStreamDestroy(stream);
											return;
										} else {
											currappinst.streams.add(new stream(currStreamName));
											streamStarted(hostname, appname, appinstname, currStreamName, currapp.getServerUrl());
										}
									} else {
										currappinst = new appinst(appinstname);
										currappinst.streams.add(new stream(currStreamName));
										currapp.appinsts.add(currappinst);
										streamStarted(hostname, appname, appinstname, currStreamName, currapp.getServerUrl());
									}
								} else {
									onStreamDestroy(stream);
								}
							} else {
								appinst currappinst = new appinst(appinstname);
								currappinst.streams.add(new stream(currStreamName));
								currapp = new app(appname, stream.getStreams().getAppInstance().getApplication().getConfigPath());
								if(currapp.isUpdaterEnabled()){
									currapp.appinsts.add(currappinst);
									streamStarted(hostname, appname, appinstname, currStreamName, currapp.getServerUrl());
								} else {
									onStreamDestroy(stream);
								}
								currhost.apps.add(currapp);
							}
						} else {
							app currapp = new app(appname, stream.getStreams().getAppInstance().getApplication().getConfigPath());
							if(currapp.isUpdaterEnabled()){
								appinst currappinst = new appinst(appinstname);
								currappinst.streams.add(new stream(currStreamName));
								currapp.appinsts.add(currappinst);
								streamStarted(hostname, appname, appinstname, currStreamName, currapp.getServerUrl());
							} else {
								onStreamDestroy(stream);
								return;
							}
							currhost = new host(hostname);
							currhost.apps.add(currapp);
							hosts.add(currhost);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				onStreamDestroy(stream);
			}
		}
		
		/**
		 * Do NOT remove
		 */
		public void onPause(IMediaStream stream, boolean isPause, double location) {
		}
		
		/**
		 * Do NOT remove
		 */
		public void onMetaData(IMediaStream stream, AMFPacket metaDataPacket) {
		}
	}

}