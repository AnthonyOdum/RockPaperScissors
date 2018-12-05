
package finalProject;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.Scanner;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class GameClient {
  private static final Logger logger = Logger.getLogger(GameClient.class.getName());

  private final ManagedChannel channel;
  private final GameGrpc.GameBlockingStub blockingStub;

  /** Construct client connecting to HelloWorld server at {@code host:port}. */
  public GameClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext()
        .build());
  }

  /** Construct client for accessing HelloWorld server using the existing channel. */
  GameClient(ManagedChannel channel) {
    this.channel = channel;
    blockingStub = GameGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }

  /** Say hello to server. */
  public int createPlayer(String name) {
    CreatePlayerRequest request = CreatePlayerRequest.newBuilder().setName(name).build();
    CreatePlayerReply response = blockingStub.createPlayer(request);
    return response.getId();
  }
  
  public List<GameName> listGames() {
    ListGamesRequest request = ListGamesRequest.newBuilder().build();
    ListGamesReply response = blockingStub.listGames(request);
    return response.getGameList();
  }
  
  public void createGame(String thisGameName, int playerID) {
    CreateGameRequest request =
        CreateGameRequest
            .newBuilder()
            .setNewGameName(thisGameName)
            .setPlayerID(playerID)
            .build();
    CreateGameReply response = blockingStub.createGame(request);
  }
  
  public void joinGame(String gameName, int playerID) {
    JoinGameRequest request =
        JoinGameRequest
            .newBuilder()
            .setGameName(gameName)
            .setPlayerID(playerID)
            .build();
    JoinGameReply response = blockingStub.joinGame(request);
  }
  
  public void makeGuess(int guess, String thisGameName, int playerID) {
    MakeGuessRequest request =
        MakeGuessRequest
            .newBuilder()
            .setGameName(thisGameName)
            .setGuess(guess)
            .setPlayerID(playerID)
            .build();
    MakeGuessReply response = blockingStub.makeGuess(request);
  }
  
  

  /**
   * Greet server. If provided, the first element of {@code args} is the name to use in the
   * greeting.
   */
  public static void main(String[] args) throws Exception {
    GameClient client;
    if (args.length == 1) {
      client = new GameClient(args[0], 50051);
    } else if (args.length == 2) {
      client = new GameClient(args[0], Integer.parseInt(args[1]));
    } else {
      client = new GameClient("localhost", 50051);
    }

    String user = "";
    int userID = 0;
     Scanner kb = new Scanner(System.in);
        
    try {
      /* Access a service running on the local machine on port 50051 */
      System.out.print("Insert name: ");
      user = kb.nextLine();
      userID = client.createPlayer(user);
      
      String gName = chooseOrCreateGame(client, userID, kb);
      
      
     
      System.out.println("Make a guess: [1 = ROCK, 2 = PAPER, 3 = SCISSORS]");
      int guess = kb.nextInt();
     
      while(!(guess == 1 || guess == 2 || guess == 3)) {
        System.out.println("Invalid guess. Please guess again: [1 = ROCK, 2 = PAPER, 3 = SCISSORS]");
        guess = kb.nextInt();
      }
     
      client.makeGuess(guess, gName, userID);
     
      //CheckGameResponse r = client.checkGame(...);
      //while (!r.isGameOver()) {
       //sleepamount of time between checks
       //r = client.checkGame(...);
      //}
     
      
    } catch (StatusRuntimeException e) {
      logger.log(Level.SEVERE, "RPC failed: {0}", e.getStatus()); 
    } finally {
      client.shutdown();
    }
  }
  
  public static String chooseOrCreateGame(GameClient client, int userID, Scanner kb) {
    while (true) {
      System.out.print("Insert Join or Create: ");
      String gOption = kb.nextLine();
      if (gOption.equalsIgnoreCase("join")) {
        System.out.println(client.listGames());
        System.out.println("Insert Game Name to Join: ");
        String gName = kb.nextLine();
        client.joinGame(gName, userID);
        return gName;
      } else if (gOption.equalsIgnoreCase("create")) {
        System.out.print("Insert Game Name: ");
        String gName = kb.nextLine();
        client.createGame(gName, userID);
        return gName;
      }
    }
      
    }
}