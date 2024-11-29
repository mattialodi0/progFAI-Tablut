package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.ourClient.interfaces.TreeSearch;
import it.unibo.ai.didattica.competition.tablut.ourClient.ourUtilities.GameHelper;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class IterativeDeepening implements TreeSearch {
    private long start_time = 0;
    private int MAX_TIME = 10_000; // massimo tempo di ricerca in millisecondi (9 secondi)
    private AtomicBoolean stopSearch = new AtomicBoolean(false); // Flag per fermare il thread
    private Action bestAction = null; // Risultato migliore trovato

    public IterativeDeepening() {
    }

    public IterativeDeepening(int timeout) {
        this.MAX_TIME = timeout * 1000;
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
        int i = 1;
        int j = 0;
        List<Action> moves = GameHelper.availableMoves(state.clone());
        if (!moves.isEmpty()) {
            bestAction = moves.get(0); // Assegna la prima mossa valida come backup
        }

        stopSearch.set(false);
        while (!stopSearch.get()) {
            long elapsedTime = System.currentTimeMillis() - start_time;
            int remainingTime = (int) (MAX_TIME - elapsedTime);

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
                    workerThread.interrupt();
                    workerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!stopSearch.get() && worker.getBestAction() != null) {
                bestAction = worker.getBestAction();
                j++;
            } else if (worker.getBestAction() != null) {
                // Aggiorna la miglior azione trovata anche se il tempo è scaduto
                if (worker.getDepth() > j) {
                    bestAction = worker.getBestAction();
                }
            }
            i++;
        }
    }

    @Override
    public Boolean hasMoreTime() {
        return (System.currentTimeMillis() - start_time) < MAX_TIME;
    }
}