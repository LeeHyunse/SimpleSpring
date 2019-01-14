package com.my.test.controller;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	private final Queue<DeferredResult<String>> responseBodyQueue = new ConcurrentLinkedQueue<>();

	/**
	 * Runnable을 사용하는 경우, 단일 메소드 run()만 있기 때문에, 인자를 받거나 예외 발생시 그 값을 반환 받을 수 없다.
	 */
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
	
	/**
	 * Callable 객체를 그냥 call 했을 때는, 현재의 스레드로 실행되지만,
	 * Callable 객체를 리턴하게 되면,
	 * 스프링에서 감지하여, 내장 ThreadPoolExecutor를 통해 비동기로 처리하게 된다.(새로운 스레드로)
	 * 서블릿 컨테이너 스레드는 종료되어 다른 요청을 받을 수 있게 되지만, Response는 계속해서 대기 상태.
	 * 이후 Callable이 종료되면, 스프링은 다시 서블릿컨테이너에게 재요청 한다.
	 */
	@RequestMapping(value = "/query2", method = RequestMethod.POST)
	@ResponseBody
	public Callable<String> callable1(Model model) throws Exception {
		System.out.println(Thread.currentThread().getId());
		Callable<String> cstr = new Callable<String>() {
			@Override
			public String call() throws Exception {
				System.out.println(Thread.currentThread().getId() + "/ 신규 스레드 시작");
				Thread.sleep(2000);
				System.out.println(Thread.currentThread().getId() + "/ 신규 스레드 종료");
				return "query2";
			}
		};
		cstr.call();
		System.out.println(Thread.currentThread().getId() + "/ 콘트롤러 마지막");
		return cstr;
	}
	
	/**
	 * 스프링은 DeferredResult에 result 값이 있으면 response를 리턴한다.
	 * 그런데 Executor가 작업을 전달받아 바로 실행하기 때문에, result가 이미 세팅되어있다면 바로 종료되게 된다.
	 * setResult로 결과를 세팅하거나, 예외가 발생해서 setErrorResult로 예외가 반환될 경우 종료.
	 * 스프링의 TaskExecutor를 통해 작업이 되는게 아니기 때문에, 직접 Runnalbe 객체를 만들어야 한다는 차이점이 존재한다.
	 */
	@RequestMapping(value = "/deferredResult1", method = RequestMethod.POST)
	@ResponseBody
	public DeferredResult<String> deferredResult1(Model model) throws InterruptedException {
		System.out.println(Thread.currentThread().getId());
		final DeferredResult<String> result = new DeferredResult<>();
		ExecutorService es = Executors.newFixedThreadPool(3);
		es.execute(() -> {
			System.out.println(Thread.currentThread().getId());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result.setResult("deferredResult1");
			System.out.println(Thread.currentThread().getId() + "/ 신규 스레드 종료");
		});
		Thread.sleep(4000);
		System.out.println(Thread.currentThread().getId() + "/ 콘트롤러 마지막");
		return result;
	}
	
	/**
	 * TaskExecutor로 돌리는 작업이 오래걸릴 경우, response 응답이 내려가지 않고 대기함
	 */
	@RequestMapping(value = "/deferredResult2", method = RequestMethod.POST)
	@ResponseBody
	public DeferredResult<String> deferredResult2(Model model) throws InterruptedException {
		System.out.println(Thread.currentThread().getId());
		final DeferredResult<String> result = new DeferredResult<>();
		ExecutorService es = Executors.newFixedThreadPool(3);
		es.execute(() -> {
			System.out.println(Thread.currentThread().getId());
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result.setResult("deferredResult2");
			System.out.println(Thread.currentThread().getId() + "/ 신규 스레드 종료");
		});
		Thread.sleep(2000);
		System.out.println(Thread.currentThread().getId() + "/ 콘트롤러 마지막");
		return result;
	}

	/**
	 * 응답으로 내려준 DeferredResult 객체를, 따로 보관하고 있다가 나중에 결과를 내려줄 수 있다.
	 * Long polling 구현
	 */
	@RequestMapping(value = "/deferredResult3", method = RequestMethod.POST)
	@ResponseBody
	public DeferredResult<String> deferredResult3(Model model) throws InterruptedException {
		 DeferredResult<String> result = new DeferredResult<String>();
        this.responseBodyQueue.add(result);
		return result;
	}
	
	/**
	 * Long polling trigger
	 */
	@RequestMapping(value = "/deferredResult4", method = RequestMethod.GET)
	@ResponseBody
	public void deferredResult4(String msg, Model model) throws InterruptedException {
		for (DeferredResult<String> result : this.responseBodyQueue) {
            result.setResult(msg);
            this.responseBodyQueue.remove(result);
        }
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
	
	
	@RequestMapping(value = "/query6", method = RequestMethod.GET)
	@ResponseBody
	public ResponseBodyEmitter query6(Model model) {
		System.out.println(Thread.currentThread().getId());
		ExecutorService es = Executors.newFixedThreadPool(3);
		ResponseBodyEmitter emitter = new ResponseBodyEmitter();
		es.execute(() -> {
			for (int i=0; i<10; i++) {
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
			for (int i=0; i<10; i++) {
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
