
package finalProject;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Game} server.
 */
public class GameServer {
  private static final Logger logger = Logger.getLogger(GameServer.class.getName());
  
  static HashMap<Integer, Player> players = new HashMap<>();
  static HashMap<String, GameState> games = new HashMap<>(); 
  
  private Server server;

  private void start() throws IOException {
    /* The port on which the server should run */
    int port = 50051;
    server = ServerBuilder.forPort(port)
        .addService(new GameImpl())
        .build()
        .start();
    logger.info("Server started, listening on " + port);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
        System.err.println("*** shutting down gRPC server since JVM is shutting down");
        GameServer.this.stop();
        System.err.println("*** server shut down");
      }
    });
  }

  private void stop() {
    if (server != null) {
      server.shutdown();
    }
  }

  /**
   * Await termination on the main thread since the grpc library uses daemon threads.
   */
  private void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }

  /**
   * Main launches the server from the command line.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    final GameServer server = new GameServer();
    server.start();
    server.blockUntilShutdown();
  }

  static class GameImpl extends GameGrpc.GameImplBase {
      
      
    @Override
    public void createPlayer(CreatePlayerRequest req, StreamObserver<CreatePlayerReply> responseObserver) {
      int iD = (int) (Math.random() * 10000) + 1000;
      CreatePlayerReply reply = CreatePlayerReply.newBuilder().setId(iD).build();
      responseObserver.onNext(reply);
      responseObserver.onCompleted();
      synchronized(players) {
        Player playerOne = new Player(req.getName(), iD);
        players.put(iD, playerOne);
        if(!req.getName().equalsIgnoreCase("stop")){
           System.out.println("Player " + players.size() + ": " + req.getName() + " - " + iD);
        }
      }
    }
    
    @Override
    public void listGames(ListGamesRequest req, StreamObserver<ListGamesReply> responseObserver) {
      ListGamesReply.Builder reply = ListGamesReply.newBuilder();
      for (GameState gs : games.values()) {
        String name = gs.getName();
        GameName newGame = GameName.newBuilder().setName(name).build();
        reply.addGame(newGame);
      }
      responseObserver.onNext(reply.build());
      responseObserver.onCompleted();
    }
    
    @Override
    public void createGame(CreateGameRequest req, StreamObserver<CreateGameReply> responseObserver) {
      CreateGameReply.Builder reply = CreateGameReply.newBuilder();
      int playerID = req.getPlayerID();
      String gNam = req.getNewGameName();
      if(players.containsKey(playerID)){
        GameState gameOne = new GameState(gNam);
        gameOne.addPlayer(players.get(playerID));
        games.put(gNam, gameOne);
      } else {
        responseObserver
            .onError(Status.INVALID_ARGUMENT
            .withDescription("Player ID not found.")
            .asRuntimeException());
        return;
      }
      responseObserver.onNext(reply.build());
      responseObserver.onCompleted();
    }
    
    @Override
    public void joinGame(JoinGameRequest req, StreamObserver<JoinGameReply> responseObserver) {
      JoinGameReply.Builder reply = JoinGameReply.newBuilder();
      int playerID = req.getPlayerID();
      String gNam = req.getGameName();
      if(!games.containsKey(gNam) || !players.containsKey(playerID)){
        responseObserver
            .onError(Status.INVALID_ARGUMENT
            .withDescription("Either game or player does not exist.")
            .asRuntimeException());
        return;
      }

      GameState g = games.get(gNam);
      System.out.println(g);
      Player challenger = players.get(playerID);
      g.addPlayer(challenger);
      games.put(gNam, g);
      responseObserver.onNext(reply.build());
      responseObserver.onCompleted();
    }
    
   @Override
    public void makeGuess(MakeGuessRequest req, StreamObserver<MakeGuessReply> responseObserver) {
      if (req.getGuess() != 1 && req.getGuess() != 2 && req.getGuess() != 3) {
        responseObserver
            .onError(Status.INVALID_ARGUMENT
            .withDescription("Guess not valid.")
            .asRuntimeException());
        return;
      }
      MakeGuessReply.Builder reply = MakeGuessReply.newBuilder();
      String gName = req.getGameName();
      int playerID = req.getPlayerID();
      if(!games.containsKey(gName)){
        responseObserver
            .onError(Status.INVALID_ARGUMENT
            .withDescription("Game not found.")
            .asRuntimeException());
        return;
      }
      GameState game = games.get(gName);
      synchronized(game) {
        if (game.p1.getID() == playerID) {
          game.setP1Guess(req.getGuess());
        } else if (game.p2.getID() == playerID) {
          game.setP2Guess(req.getGuess());
        } else {
          responseObserver
            .onError(Status.INVALID_ARGUMENT
            .withDescription("Player ID not found.")
            .asRuntimeException());
          return;
        }
      }
      responseObserver.onNext(reply.build());
      responseObserver.onCompleted();
    }
   
  }
}
