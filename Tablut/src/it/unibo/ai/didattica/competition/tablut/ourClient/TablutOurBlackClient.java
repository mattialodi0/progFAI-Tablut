package it.unibo.ai.didattica.competition.tablut.ourClient;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.ourClient.TablutOurClient;

public class TablutOurBlackClient {
	
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		String[] array = new String[]{"BLACK"};
		if (args.length>0){
			array = new String[]{"BLACK", args[0]};
		}
		TablutOurClient.main(array);
	}

}
