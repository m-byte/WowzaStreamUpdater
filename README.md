WowzaStreamUpdater
==================

Wowza Media Server plugin for sending notifications on stream status changes


Compilation
-----------
Use Wowza IDE (http://www.wowza.com/media-server/developers) or the Wowza plugin for Eclipse to compile.

Setup
-----
1. Set a proper VHost name in [wowza-install-dir]/conf/VHosts.xml
2. Add the following code to the &lt;Modules&gt; section of your application's Application.xml:
<pre>      &lt;Module&gt;
        &lt;Name&gt;StreamUpdater&lt;/Name&gt;
        &lt;Description&gt;WowzaStreamUpdater&lt;/Description&gt;
        &lt;Class&gt;de.hhu.wms.streamupdater.StreamUpdater&lt;/Class&gt;
      &lt;/Module&gt;</pre>
3. Add the following code to the &lt;Properties> section of your application's Application.xml:
<pre>      &lt;Property&gt;
        &lt;Name&gt;streamupdaterurl&lt;/Name&gt;
        &lt;Value&gt;http://[your-handler-url]&lt;/Value&gt;
      &lt;/Property&gt;</pre>
3. Copy WowzaStreamUpdater.jar to [wowza-install-dir]/lib
