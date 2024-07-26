package com.baeldung.product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

@SpringBootApplication
public class ProductServiceApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

	@Override
	public void run(String... args) {

		// connection to the Redis server
		Jedis redis = new Jedis("redis", 6379);

		// KEY-VALUE operations
		System.out.println("-----------------------------KEY-VALUE operation.");
		System.out.println("Insert key 'name'");
		redis.set("name", "pippo");
		System.out.println("The value of 'name' is : " + redis.get("name"));
		System.out.println("Does the key 'name' exist before 'del' operation? : " + redis.exists("name"));
		redis.del("name");
		System.out.println("Does the key 'name' exist after 'del' operation? : " + redis.exists("name"));

		// LISTS operations
		System.out.println("-----------------------------LISTS operation.");
		System.out.println("Insert 'bike:1' to the head");
		redis.lpush("bikes:repairs", "bike:1");
		System.out.println("Insert 'bike:2' to the tail");
		redis.rpush("bikes:repairs", "bike:2");
		System.out.println("Take element from the head : " + redis.lpop("bikes:repairs"));
		System.out.println("Take element from the tail : " + redis.rpop("bikes:repairs"));

		// SETS operations
		System.out.println("-----------------------------SETS operation.");
		System.out.println("Create set 'SocialMedia'");
		redis.sadd("SocialMedia", "Facebook", "Twitter", "Whatsapp");
		System.out.println("The elements of the set are : " + redis.smembers("SocialMedia"));
		System.out.println("The number of elements of the set are : " + redis.scard("SocialMedia"));

		// HASHES operations
		System.out.println("-----------------------------SETS operation.");
		System.out.println("Create hash 'user:123' with 'name' and 'country'");
		redis.hset("user:123", "name", "Charlie");
		redis.hset("user:123", "country", "USA");
		System.out.println("The elements of the hash before 'del country' operation are : " + redis.hgetAll("user:123"));
		redis.hdel("user:123", "country");
		System.out.println("The elements of the hash after 'del country' operation are : " + redis.hgetAll("user:123"));

		// Transactions
		System.out.println("-----------------------------Transactions.");
		System.out.println("Start sequence of operations:");
		Transaction t = redis.multi();
		System.out.println("Create 'set' operatin;");
		t.set("name", "Alice");
		System.out.println("Create 'haset' operatin;");
		t.hset("user:123", "username", "Alice");
		System.out.println("Execute sequence of operations : " + t.exec());
	}
}
