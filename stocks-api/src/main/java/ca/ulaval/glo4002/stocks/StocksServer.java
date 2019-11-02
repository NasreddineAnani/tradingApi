package ca.ulaval.glo4002.stocks;

import org.springframework.boot.SpringApplication;

public class StocksServer implements Runnable {
  private String[] args;

  public static void main(String[] args) {
    new StocksServer(args).run();
  }

  public StocksServer(String[] args) {
    this.args = args;
  }

  @Override
  public void run() {
    SpringApplication.run(StocksSpringApplication.class, args);
  }
}
