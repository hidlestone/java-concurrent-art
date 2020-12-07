package chapter04;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * @author: payn
 * @date: 2020/12/4 13:53
 */
public class ConnectionDriver {

	static class ConnectionHandler implements InvocationHandler {
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("commit")) {
				TimeUnit.MILLISECONDS.sleep(100);
			}
			return null;
		}
	}

	//创建一个connection的代理，在commit时休眠100毫秒
	public static final Connection createConnection() {
		return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(),
				new Class<?>[]{Connection.class}, new ConnectionHandler());
	}

}
