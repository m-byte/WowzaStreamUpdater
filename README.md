WowzaStreamUpdater
==================

Wowza Media Server plugin for sending notifications on stream status changes


Compilation:
============
Use Wowza IDE (http://www.wowza.com/media-server/developers) or the Wowza plugin for Eclipse to compile.

Setup:
======
1. Set a proper VHost name in [wowza-install-dir]/conf/VHosts.xml
2. Add the following code to the <Modules> section of your application's Application.xml:
      <Module>
        <Name>StreamUpdater</Name>
        <Description>HHUD StreamUpdater</Description>
        <Class>de.hhu.wms.streamupdater.StreamUpdater</Class>
      </Module>
3. Add the following code to the <Properties> section of your application's Application.xml:
      <Property>
  			<Name>streamupdaterurl</Name>
				<Value>http://[your-handler-url]</Value>
			</Property>
3. Copy WowzaStreamUpdater.jar to [wowza-install-dir]/lib
