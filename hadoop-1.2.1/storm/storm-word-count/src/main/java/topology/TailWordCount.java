package topology;

import spouts.TailSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import bolts.ContadorPalabrasBolt;
import bolts.SeparadorPalabrasBolt;

public class TailWordCount {

	public static void main(String[] args) throws InterruptedException {
		// Definicion de la topología
		TopologyBuilder constructor = new TopologyBuilder();
		constructor.setSpout("tail", new TailSpout("/tmp/test"));
		constructor.setBolt("separador-palabras", new SeparadorPalabrasBolt())
				.shuffleGrouping("tail");
		constructor.setBolt("contador-palabras", new ContadorPalabrasBolt())
				.fieldsGrouping("separador-palabras", new Fields("palabra"));

		// Configuracion
		Config conf = new Config();
		conf.put("archivoPalabras", "/tmp/test");
		conf.setDebug(true);
		conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);

		// Arrancar la topología
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("tail-word-count", conf,
				constructor.createTopology());
		Thread.sleep(1000);
		cluster.shutdown();
	}

}
