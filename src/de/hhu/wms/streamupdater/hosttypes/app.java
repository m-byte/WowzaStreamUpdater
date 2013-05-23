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

package de.hhu.wms.streamupdater.hosttypes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class app {
	public LinkedList<appinst> appinsts;
	private String name;
	private String serverurl;

	public app(String name, String confPath) throws UnsupportedEncodingException{
		this.name = URLEncoder.encode(name, "UTF-8");
		this.appinsts = new LinkedList<appinst>();
		this.serverurl = null;
		urlfromxml(confPath);
	}

	private void setServerUrl(String serverurl){
		this.serverurl = serverurl;
	}

	private void urlfromxml(String path){
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			DefaultHandler handler = new DefaultHandler() {
				boolean application = false;
				boolean properties = false;
				boolean property = false;
				boolean name = false;
				boolean value = false;
				boolean found = false;
				@Override
				public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
					if(qName.equalsIgnoreCase("Application")) {
						application = true;
					}
					if(qName.equalsIgnoreCase("Properties")) {
						properties = true;
					}
					if (qName.equalsIgnoreCase("Property")) {
						property = true;
					}
					if (qName.equalsIgnoreCase("Name")) {
						name = true;
					}
					if (qName.equalsIgnoreCase("Value")) {
						value = true;
					}
				}

				@Override
				public void endElement(String uri, String localName, String qName) throws SAXException {
					if(qName.equalsIgnoreCase("Application")) {
						application = false;
					}
					if(qName.equalsIgnoreCase("Properties")) {
						properties = false;
					}
					if(qName.equalsIgnoreCase("Property")) {
						property = false;
					}
					if(qName.equalsIgnoreCase("Name")) {
						name = false;
					}
					if(qName.equalsIgnoreCase("Value")) {
						value = false;
					}
				}

				@Override
				public void characters(char ch[], int start, int length) throws SAXException {
					if(application && properties && property && name && (new String(ch, start, length)).equalsIgnoreCase("streamupdaterurl"))
						found = true;
					if(application && properties && property && value && found){
						setServerUrl(new String(ch, start, length));
						found = false;
					}
				}
			};
			saxParser.parse(path, handler);
		} catch(Exception e) {
			setServerUrl(null);
		}
	}

	public boolean isUpdaterEnabled(){
		try {
			return serverurl != null && !serverurl.isEmpty();
		} catch(Exception e){
			return false;
		}
	}

	public String getServerUrl(){
		return serverurl;
	}

	public String getName() {
		return name;
	}

	public appinst getAppInst(String name) throws UnsupportedEncodingException{
		for(appinst currappinst:appinsts){
			if(currappinst.getName().equals(URLEncoder.encode(name, "UTF-8")))
				return currappinst;
		}
		return null;
	}
}
