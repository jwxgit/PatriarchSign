package com.jwx.patriarchsign.data.manager;


/**
 * 线程池管理类
 */
public class ThreadManager
{
	private static ThreadPoolProxy	mLongPool;
	private static Object mLongLock = new Object();

	private static ThreadPoolProxy	mDownloadPool;
	private static Object mDownloadLock = new Object();

	public static ThreadPoolProxy getLongRunPool()
	{
		if (mLongPool == null)
		{
			synchronized (mLongLock)
			{
				if (mLongPool == null)
				{
					mLongPool = new ThreadPoolProxy(3, 3, 5L);
				}
			}
		}
		return mLongPool;
	}
	
	/**
	 * 专门负责下载的线程池
	 * @return
	 */
	public static ThreadPoolProxy getDownloadPool()
	{
		if (mDownloadPool == null)
		{
			synchronized (mDownloadLock)
			{
				if (mDownloadPool == null)
				{
					mDownloadPool = new ThreadPoolProxy(1, 1, 5L);
				}
			}
		}
		return mDownloadPool;
	}

}
