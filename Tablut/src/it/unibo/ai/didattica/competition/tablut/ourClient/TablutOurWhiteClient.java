package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.ourClient.TablutOurClient;

public class TablutOurWhiteClient {
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		String[] array = new String[]{"WHITE"};
		if (args.length>0){
			array = new String[]{"WHITE", args[0]};
		}
		TablutOurClient.main(array);
	}


}
