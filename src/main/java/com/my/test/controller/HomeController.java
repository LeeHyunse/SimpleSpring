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
	 * Runnable�� ����ϴ� ���, ���� �޼ҵ� run()�� �ֱ� ������, ���ڸ� �ްų� ���� �߻��� �� ���� ��ȯ ���� �� ����.
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
				throw new RuntimeException("�������� ���� �߻�");
			}
		};
		
		Thread t = new Thread(runnable);
		t.start();
		System.out.println("End");
		return "query";
	}
	
	/**
	 * Callable ��ü�� �׳� call ���� ����, ������ ������� ���������,
	 * Callable ��ü�� �����ϰ� �Ǹ�,
	 * ���������� �����Ͽ�, ���� ThreadPoolExecutor�� ���� �񵿱�� ó���ϰ� �ȴ�.(���ο� �������)
	 * ���� �����̳� ������� ����Ǿ� �ٸ� ��û�� ���� �� �ְ� ������, Response�� ����ؼ� ��� ����.
	 * ���� Callable�� ����Ǹ�, �������� �ٽ� ���������̳ʿ��� ���û �Ѵ�.
	 */
	@RequestMapping(value = "/query2", method = RequestMethod.POST)
	@ResponseBody
	public Callable<String> callable1(Model model) throws Exception {
		System.out.println(Thread.currentThread().getId());
		Callable<String> cstr = new Callable<String>() {
			@Override
			public String call() throws Exception {
				System.out.println(Thread.currentThread().getId() + "/ �ű� ������ ����");
				Thread.sleep(2000);
				System.out.println(Thread.currentThread().getId() + "/ �ű� ������ ����");
				return "query2";
			}
		};
		cstr.call();
		System.out.println(Thread.currentThread().getId() + "/ ��Ʈ�ѷ� ������");
		return cstr;
	}
	
	/**
	 * �������� DeferredResult�� result ���� ������ response�� �����Ѵ�.
	 * �׷��� Executor�� �۾��� ���޹޾� �ٷ� �����ϱ� ������, result�� �̹� ���õǾ��ִٸ� �ٷ� ����ǰ� �ȴ�.
	 * setResult�� ����� �����ϰų�, ���ܰ� �߻��ؼ� setErrorResult�� ���ܰ� ��ȯ�� ��� ����.
	 * �������� TaskExecutor�� ���� �۾��� �Ǵ°� �ƴϱ� ������, ���� Runnalbe ��ü�� ������ �Ѵٴ� �������� �����Ѵ�.
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
			System.out.println(Thread.currentThread().getId() + "/ �ű� ������ ����");
		});
		Thread.sleep(4000);
		System.out.println(Thread.currentThread().getId() + "/ ��Ʈ�ѷ� ������");
		return result;
	}
	
	/**
	 * TaskExecutor�� ������ �۾��� �����ɸ� ���, response ������ �������� �ʰ� �����
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
			System.out.println(Thread.currentThread().getId() + "/ �ű� ������ ����");
		});
		Thread.sleep(2000);
		System.out.println(Thread.currentThread().getId() + "/ ��Ʈ�ѷ� ������");
		return result;
	}

	/**
	 * �������� ������ DeferredResult ��ü��, ���� �����ϰ� �ִٰ� ���߿� ����� ������ �� �ִ�.
	 * Long polling ����
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
