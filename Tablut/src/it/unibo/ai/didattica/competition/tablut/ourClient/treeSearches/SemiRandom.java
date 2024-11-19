package it.unibo.ai.didattica.competition.tablut.ourClient.treeSearches;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.ourClient.GameHelper;
import it.unibo.ai.didattica.competition.tablut.ourClient.evaluations.Heuristics;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.simulator.TablutGame;


public class SemiRandom{

    public Action randMove(State state) {
        List<int[]> escapingTiles = Arrays.asList(
            new int[] { 0, 1 },
            new int[] { 0, 2 },
            new int[] { 0, 6 },
            new int[] { 0, 7 },
            new int[] { 1, 0 },
            new int[] { 1, 8 },
            new int[] { 2, 0 },
            new int[] { 2, 8 },
            new int[] { 6, 8 },
            new int[] { 7, 8 },
            new int[] { 6, 0 },
            new int[] { 7, 0 },
            new int[] { 8, 1 },
            new int[] { 8, 2 },
            new int[] { 8, 6 },
            new int[] { 8, 7 }
        );

        List<int[]> availableEscapingTiles = escapingTiles.stream()
            .filter(GameHelper.populateEmptyList(state)::contains)
            .collect(Collectors.toList());

        List<int[]> whitePawns = new ArrayList<int[]>();
        List<int[]> blackPawns = new ArrayList<int[]>();
        List<int[]> empty = new ArrayList<int[]>();
        int[] selected = new int[2];
        int[] escape = new int[2];
        int[] emptySide = null;
        int[] emptySideKing = null;
        int[] emptySideWPawn = null;

       
        if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
            whitePawns = GameHelper.populatePawnList(state);

            // NON SICURO CHE SIA GIUSTO
            state.setTurn(Turn.BLACK);

            blackPawns = GameHelper.populatePawnList(state);
            empty = GameHelper.populateEmptyList(state);

            // NON SICURO CHE SIA GIUSTO
            state.setTurn(Turn.WHITE);

            boolean capture = false;
            boolean king = false;

            Action a = null;
            try {
                a = new Action("z0", "z0", State.Turn.WHITE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            // per ogni pedone ancora in vita, devo vedere se riesco a mangiarne un altro
            // oppure se riesco a difendermi

            /*  
                Logica: per ogni pedone, se il pedone è il re E può scappare, allora l'azione sarà muovere
                il re verso la casella di fuga.
                Se esiste un pedone, tale che riesce ad occupare la quarta casella e catturare un nero,
                allora l'azione sarà muovere il pedone verso quella casella
                Se, per ogni pedone, o il re non ha una casella di fuga immediata, oppure non esiste un pedone tale che
                esso riesca a catturare il nero, allora metto mossa a caso.
            */
            for (int[] wPawn : whitePawns) {
                if (state.getPawn(wPawn[0], wPawn[1]).equalsPawn(State.Pawn.KING.toString())) {
                    for (int[] escapeTile : availableEscapingTiles) {
                        if (Heuristics.canReach(wPawn, escapeTile, empty)) {
                            selected = wPawn;
                            escape = escapeTile;
                            king = true;
                            break;
                        }
                    }
                } else {
                    for (int[] bPawn : blackPawns) {
                        int count = 0;
                        boolean canCapture = false;
                        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

                        for (int[] dir : directions) {
                            int newRow = bPawn[0] + dir[0];
                            int newCol = bPawn[1] + dir[1];
                            if (newRow < 0 || newRow > 8 || newCol < 0 || newCol > 8) continue;
                            if (state.getPawn(newRow, newCol).equalsPawn(State.Pawn.WHITE.toString())
                                    && !state.getPawn(newRow, newCol).equalsPawn(wPawn.toString())) {
                                count++;
                            } else if (state.getPawn(newRow, newCol).equalsPawn(State.Pawn.EMPTY.toString())
                                    && GameHelper.canPawnMove(state, wPawn, newRow, newCol)) {
                                canCapture = true;
                                emptySide = new int[]{newRow, newCol};
                            }
                        }

                        if (count == 3 && canCapture) {
                            selected = wPawn;
                            capture = true;
                            break;
                        }
                    }
                }
            }

            if (!capture && !king) {
                if (whitePawns.size() > 1) {
                    selected = whitePawns.get(new Random().nextInt(whitePawns.size() - 1));
                } else {
                    selected = whitePawns.get(0);
                }
            }
            String from = state.getBox(selected[0], selected[1]);

            String to = "";
            if (king) {
                to = state.getBox(escape[0], escape[1]);
            } else if (capture || (emptySide != null && emptySide.length > 0)) {
                to = state.getBox(emptySide[0], emptySide[1]);
            } else {
                selected = empty.get(new Random().nextInt(empty.size() - 1));
                to = state.getBox(selected[0], selected[1]);
            }

            

            // scelgo mossa random finchè non ne trovo una che vada bene
            boolean found = false;
            while (!found) {
                try {
                    // Attempt to create a valid action
                    a = new Action(from, to, State.Turn.WHITE);
                    // Check if the move is valid
                    if (TablutGame.checkMove(state, a)) {
                        found = true;
                    } else {
                        // If checkMove fails, pick new random positions
                        selected = empty.get(new Random().nextInt(empty.size() - 1));
                        to = state.getBox(selected[0], selected[1]);
                    }
                } catch (Exception e) {
                    // If an exception occurs (e.g., invalid format), retry with new coordinates
                    selected = empty.get(new Random().nextInt(empty.size() - 1));
                    to = state.getBox(selected[0], selected[1]);

                    // Optionally, log or print the error message for debugging
                    System.out.println("Invalid move attempted. Retrying...");
                }
            }
            /*while (!found) {
                try {
                    a = new Action(from, to, State.Turn.WHITE);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                
                try {
                    TablutGame.checkMove(state, a);
                    found = true;
                } catch (Exception e) {
                    // TODO sistemare questa
                    selected = empty.get(new Random().nextInt(empty.size()));
                    to = state.getBox(selected[0], selected[1]);

                    to = state.getBox(selected[0], selected[1]);

                    try {
                        a = new Action(from, to, State.Turn.WHITE);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }*/
            return a;

        } 
        else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {

                whitePawns = GameHelper.populatePawnList(state);
    
                // NON SICURO CHE SIA GIUSTO
                state.setTurn(Turn.WHITE);
    
                blackPawns = GameHelper.populatePawnList(state);
                empty = GameHelper.populateEmptyList(state);
    
                // NON SICURO CHE SIA GIUSTO
                state.setTurn(Turn.BLACK);
    
                boolean found = false;
                boolean captureKing = false;
                boolean capturePawn = false;
    
    
                Action a = null;
                try {
                    a = new Action("z0", "z0", State.Turn.BLACK);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
    
                // LOGICA: priorità a catturare il re con la cattura a diamante, altrimenti 
                // priorità a catturare il pedone con la cattura a diamante, altrimenti
                // mossa random
                for(int[] bPawn: blackPawns){
                    for (int[] wPawn : blackPawns) {
                        int count = 0;
                        boolean canCaptureKing = false;
                        boolean canCapturePawn = false;
                        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                        if(state.getPawn(wPawn[0], wPawn[1]).equalsPawn(State.Pawn.KING.toString())){
                            for (int[] dir : directions) {
                                int newRow = wPawn[0] + dir[0];
                                int newCol = wPawn[1] + dir[1];
                                if (newRow < 0 || newRow > 8 || newCol < 0 || newCol > 8) continue;
                                if (state.getPawn(newRow, newCol).equalsPawn(State.Pawn.BLACK.toString())
                                        && !state.getPawn(newRow, newCol).equalsPawn(bPawn.toString())) {
                                    count++;
                                } else if (state.getPawn(newRow, newCol).equalsPawn(State.Pawn.EMPTY.toString())
                                        && GameHelper.canPawnMove(state, bPawn, newRow, newCol)) {
                                    canCaptureKing = true;
                                    emptySideKing = new int[]{newRow, newCol};
                                }
                            }
    
                            if (count == 3 && canCaptureKing) {
                                selected = bPawn;
                                captureKing = true;
                                break;
                            }
                        }else if(state.getPawn(bPawn[0], bPawn[1]).equalsPawn(State.Pawn.WHITE.toString())) {
                            for (int[] dir : directions) {
                                int newRow = wPawn[0] + dir[0];
                                int newCol = wPawn[1] + dir[1];
                                if (newRow < 0 || newRow > 8 || newCol < 0 || newCol > 8) continue;
                                if (state.getPawn(newRow, newCol).equalsPawn(State.Pawn.BLACK.toString())
                                        && !state.getPawn(newRow, newCol).equalsPawn(bPawn.toString())) {
                                    count++;
                                } else if (state.getPawn(newRow, newCol).equalsPawn(State.Pawn.EMPTY.toString())
                                        && GameHelper.canPawnMove(state, bPawn, newRow, newCol)) {
                                    canCapturePawn = true;
                                    emptySideWPawn = new int[]{newRow, newCol};
                                }
                            }
    
                            if (count == 3 && canCapturePawn) {
                                selected = bPawn;
                                capturePawn = true;
                                break;
                            }
                        }
                    }
                }
                if (!capturePawn && !captureKing) {
                    if (whitePawns.size() > 1) {
                        selected = whitePawns.get(new Random().nextInt(whitePawns.size() - 1));
                    } else {
                        selected = whitePawns.get(0);
                    }
                }
                String from = state.getBox(selected[0], selected[1]);
    
                String to = "";
                if (captureKing) {
                    to = state.getBox(emptySideKing[0], emptySideKing[1]);
                } else if (capturePawn && !captureKing) {
                    to = state.getBox(emptySideWPawn[0], emptySideWPawn[1]);
                } else {
                    selected = empty.get(new Random().nextInt(empty.size() - 1));
                    to = state.getBox(selected[0], selected[1]);
                }
    
                
                // scelgo mossa random finchè non ne trovo una che vada bene
                found = false;
                while (!found) {
                    try {
                        // Attempt to create a valid action
                        a = new Action(from, to, State.Turn.BLACK);
                        // Check if the move is valid
                        if (TablutGame.checkMove(state, a)) {
                            found = true;
                        } else {
                            // If checkMove fails, pick new random positions
                            selected = empty.get(new Random().nextInt(empty.size() - 1));
                            to = state.getBox(selected[0], selected[1]);
                        }
                    } catch (Exception e) {
                        // If an exception occurs (e.g., invalid format), retry with new coordinates
                        selected = empty.get(new Random().nextInt(empty.size() - 1));
                        to = state.getBox(selected[0], selected[1]);
    
                        // Optionally, log or print the error message for debugging
                        System.out.println("Invalid move attempted. Retrying...");
                    }
                }
                return a;
            }
            else return null;
        }
}