package com.my.test.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Handles requests for the application home page.
 */
@Controller

public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/query1", method = RequestMethod.POST)
	@ResponseBody
	public String query1(Model model) throws InterruptedException {
		System.out.println(Thread.currentThread().getId());
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					System.out.println(Thread.currentThread().getId());
					Thread.sleep(5000);
					System.out.println("??");
				} catch (InterruptedException e) {
				}
				throw new RuntimeException("고의적인 예외 발생");
			}
		};
		
		Thread t = new Thread(runnable);
		t.start();
		System.out.println("End");
		return "query";
	}
	
	
	
	@RequestMapping(value = "/query2", method = RequestMethod.POST)
	@ResponseBody
	public String query2(Model model) {
		System.out.println(Thread.currentThread().getId());
		ExecutorService es = Executors.newFixedThreadPool(3);
		Future<String> futureStr = es.submit(new Callable<String>() {
			@Override
			public String call() throws InterruptedException {
				System.out.println(Thread.currentThread().getId());
				Thread.sleep(5000);
				System.out.println("??");
				return "query2";
			}
		});
		System.out.println(futureStr);
		System.out.println("End");
		return "query";
	}
	
	@RequestMapping(value = "/query3", method = RequestMethod.POST)
	@ResponseBody
	public Callable<String> query3(Model model) {
		System.out.println(Thread.currentThread().getId());
		return () -> {
			System.out.println(Thread.currentThread().getId());
			Thread.sleep(5000);
			System.out.println("??");
			return "query3";
		};
	}
	
	@RequestMapping(value = "/query4", method = RequestMethod.POST)
	@ResponseBody
	public DeferredResult<String> query4(Model model) {
		System.out.println(Thread.currentThread().getId());
		final DeferredResult<String> result = new DeferredResult<>();
		ExecutorService es = Executors.newFixedThreadPool(3);
		es.execute(() -> {
			System.out.println(Thread.currentThread().getId());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result.setResult("query4");
			System.out.println("??");
		});
		return result;
	}
	
	@RequestMapping(value = "/query5", method = RequestMethod.POST)
	@ResponseBody
	public CompletableFuture<String> query5(Model model) {
		System.out.println(Thread.currentThread().getId());
		ExecutorService es = Executors.newFixedThreadPool(3);
		return CompletableFuture.supplyAsync(() -> {
			System.out.println(Thread.currentThread().getId());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("??");
			return "query5";
		}, es);
	}
	
	
	@RequestMapping(value = "/query6", method = RequestMethod.POST)
	@ResponseBody
	public ResponseBodyEmitter query6(Model model) {
		System.out.println(Thread.currentThread().getId());
		ExecutorService es = Executors.newFixedThreadPool(3);
		ResponseBodyEmitter emitter = new ResponseBodyEmitter();
		es.execute(() -> {
			for (int i=0; i<5; i++) {
				try {
					System.out.println(Thread.currentThread().getId() + "," + "i="+i);
					Thread.sleep(1000);
					emitter.send("i="+i);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			emitter.complete();
		});
		return emitter;
	}
	
	@RequestMapping(value = "/query7", method = RequestMethod.GET)
	@ResponseBody
	public SseEmitter query7(Model model) {
		System.out.println(Thread.currentThread().getId());
		ExecutorService es = Executors.newFixedThreadPool(3);
		SseEmitter emitter = new SseEmitter();
		es.execute(() -> {
			for (int i=0; i<5; i++) {
				try {
					System.out.println(Thread.currentThread().getId() + "," + "i="+i);
					Thread.sleep(1000);
					emitter.send("i="+i);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			emitter.complete();
		});
		return emitter;
	}
}
