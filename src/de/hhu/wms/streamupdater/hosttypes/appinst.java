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

public class appinst {
	public LinkedList<stream> streams;
	private String name;

	public appinst(String name) throws UnsupportedEncodingException{
		this.name = URLEncoder.encode(name, "UTF-8");
		this.streams = new LinkedList<stream>();
	}

	public String getName() {
		return name;
	}
	
	public stream getStream(String name) throws UnsupportedEncodingException{
		for(stream currstream:streams){
			if(currstream.getName().equals(URLEncoder.encode(name, "UTF-8")))
				return currstream;
		}
		return null;
	}
}