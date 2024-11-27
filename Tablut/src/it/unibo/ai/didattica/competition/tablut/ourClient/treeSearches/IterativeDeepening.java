package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.ourClient.ourUtilities.GameHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class IterativeDeepening implements TreeSearch {
    private long start_time = 0;
    private static final int MAX_TIME = 10_000; // massimo tempo di ricerca in millisecondi (9 secondi)
    private AtomicBoolean stopSearch = new AtomicBoolean(false); // Flag per fermare il thread
    private Action bestAction = null; // Risultato migliore trovato

    public IterativeDeepening() {
    }

    @Override
    public Action searchTree(State state) {
        this.start_time = System.currentTimeMillis();
        // Crea un thread per la ricerca
        Thread searchThread = new Thread(() -> iterativeSearch(state.clone()));
        searchThread.start();

        // Controlla il tempo rimanente nel thread principale
        while (System.currentTimeMillis() - start_time < MAX_TIME) {
            try {
                Thread.sleep(100); // attesa breve per evitare uso intensivo della CPU
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Tempo scaduto, ferma la ricerca
        stopSearch.set(true); // Imposta il flag per fermare il thread di ricerca
        try {
            searchThread.join(); // Attendi la fine del thread di ricerca
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Se non abbiamo trovato una mossa valida, gestiamo il caso
        if (bestAction == null) {
            System.out.println("Errore: nessuna azione trovata. Uso l'azione di fallback.");
        }

        return bestAction; // Ritorna la migliore azione trovata
    }

    private void iterativeSearch(State state) {
        int i = 0;
        List<Action> moves = GameHelper.availableMoves(state.clone());
        if (!moves.isEmpty()) {
            bestAction = moves.get(0); // Assegna la prima mossa valida come backup
        }
    
        /* 
        long previousDepthTime = 0; // Tempo impiegato per completare la profondità precedente
        double growthFactor = 1.5;  // Fattore di crescita esponenziale (ad esempio, base 2)
        */

        stopSearch.set(false);
        int j = 0;
        while (!stopSearch.get()) {
            long elapsedTime = System.currentTimeMillis() - start_time;
            int remainingTime = (int) (MAX_TIME - elapsedTime);
    
            // Calcola il tempo stimato per la prossima profondità con un fattore di crescita esponenziale
            
            /*
            long estimatedTimeForNextDepth = (previousDepthTime > 0) 
                                                ? (long) (previousDepthTime * Math.pow(growthFactor, i))
                                                : 1000;
            System.out.println("Depth: " + i + " ElapsedTime: " + elapsedTime/1000 + "s RemainingTime: " + 
                           remainingTime/1000 + "s EstimatedTime: " + estimatedTimeForNextDepth/1000 + "s");
    
            // Se il tempo rimanente è troppo poco per completare la prossima profondità, interrompi
            if (remainingTime <= estimatedTimeForNextDepth) break;
            

            long depthStartTime = System.currentTimeMillis();
            */
    
            MinMaxRunnable worker = new MinMaxRunnable(i, stopSearch, state);
            Thread workerThread = new Thread(worker);
            workerThread.start();
    
            try {
                workerThread.join(remainingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    
            if (workerThread.isAlive()) {
                stopSearch.set(true);
                try {
                    workerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    
            /* 
            if (!stopSearch.get() && worker.getBestAction() != null) {
                bestAction = worker.getBestAction();
                System.out.println("Aggiornata bestAction alla profondita " + i + ": " + bestAction.toString() + " con eval: " + worker.getScore());
            }
            */

            if (!stopSearch.get() && worker.getBestAction() != null) {
                bestAction = worker.getBestAction();
                System.out.println("Aggiornata bestAction alla profondita " + i + ": " + bestAction.toString() + " con eval: " + worker.getScore());
                j++;
            } else if (worker.getBestAction() != null) {
                // Aggiorna la miglior azione trovata anche se il tempo è scaduto
                if(worker.getDepth() >= j) {
                    bestAction = worker.getBestAction();
                    System.out.println("2: Aggiornata bestAction alla profondita " + i + ": " + bestAction.toString() + " con eval: " + worker.getScore());
                }
            }
            // Calcola il tempo impiegato per questa profondità
            // previousDepthTime = System.currentTimeMillis() - depthStartTime;
            //System.out.println("Profondita corrente: " + i + ", Tempo impiegato: " + previousDepthTime + "ms");
    
            i++;
        }
    
        System.out.println("Fine della ricerca iterativeSearch, profondita raggiunta: " + (i));
    }

    @Override
    public Boolean hasMoreTime() {
        return (System.currentTimeMillis() - start_time) < MAX_TIME;
    }
}