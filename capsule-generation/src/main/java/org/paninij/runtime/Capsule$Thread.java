/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * For more details and the latest version of this code please see
 * http://paninij.org
 * 
 * Contributor(s): Hridesh Rajan
 */

package org.paninij.runtime;

import java.util.concurrent.locks.ReentrantLock;

public abstract class Capsule$Thread extends Thread implements Capsule
{
	protected volatile Object[] panini$capsule$objects;
	protected volatile int panini$capsule$head, panini$capsule$tail, panini$capsule$size;
	public volatile int panini$ref$count;
	protected final ReentrantLock queueLock = new ReentrantLock();
	public static final int PANINI$SHUTDOWN = -1;
	public static final int PANINI$EXIT = -2;

	protected Capsule$Thread() {
		panini$capsule$objects = new Object[10];
		panini$capsule$head = 0;
		panini$capsule$tail = 0;
		panini$capsule$size = 0;
		panini$ref$count = 0;
	}

	protected final void panini$extendQueue() {
		assert (panini$capsule$tail >= panini$capsule$objects.length);
		Object[] newObjects = new Object[panini$capsule$objects.length + 10];
		if (panini$capsule$tail <= panini$capsule$head) {
			System.arraycopy(panini$capsule$objects, panini$capsule$head,
					newObjects, 0, panini$capsule$objects.length
							- panini$capsule$head);
			System.arraycopy(panini$capsule$objects, 0, newObjects,
					panini$capsule$objects.length - panini$capsule$head,
					panini$capsule$tail);
		} else
			System.arraycopy(panini$capsule$objects, panini$capsule$head,
					newObjects, 0, panini$capsule$tail - panini$capsule$head);
		panini$capsule$head = 0;
		panini$capsule$tail = panini$capsule$size;
		panini$capsule$objects = newObjects;
	}

	/**
	 * Checks to ensure whether this capsule's queue can accomodate numElems
	 * number of elements, and if not extends it.
	 * 
	 * @param numElems
	 */
	protected final void panini$ensureSpace(int numElems) {
		if (panini$capsule$head < panini$capsule$tail) {
			if (panini$capsule$objects.length
					+ (panini$capsule$head - panini$capsule$tail) < numElems)
				if (panini$capsule$size != 0)
					panini$extendQueue();
		} else if (panini$capsule$head - panini$capsule$tail < numElems)
			if (panini$capsule$size != 0)
				panini$extendQueue();
	}

	/**
	 * Extracts and returns the first duck from the capsule's queue. This method
	 * blocks if there are no ducks in the queue.
	 * 
	 * precondition: it is assumed that the lock queueLock is held before
	 * calling this method.
	 * 
	 * @return the first available duck in the capsule's queue.
	 */
	@SuppressWarnings("rawtypes")
	protected final synchronized ProcInvocation getNextProcInvocation() {
	    /*
		if (this.panini$capsule$size <= 0)
			panini$blockCapsule();
		panini$capsule$size--;
		Panini$Duck d = (Panini$Duck) panini$capsule$objects[panini$capsule$head++];
		if (panini$capsule$head >= panini$capsule$objects.length)
			panini$capsule$head = 0;
		return d;
		*/
	    throw new UnsupportedOperationException();
	}

	private final void panini$blockCapsule() {
		nomessages: while (this.panini$capsule$size <= 0)
			try {
				wait();
			} catch (InterruptedException e) {
				continue nomessages;
			}
	}

	protected final boolean panini$empty() {
		return panini$capsule$size == 0;
	}

	/**
	 * Causes the current capsule to sleep (temporarily cease execution) for the
	 * specified number of milliseconds, subject to the precision and accuracy
	 * of system timers and schedulers. The capsule does not lose ownership of
	 * any monitors.
	 * 
	 * @param millis
	 *            the length of time to sleep in milliseconds
	 * @throws IllegalArgumentException
	 *             - if the value of millis is negative
	 * 
	 */
	public void yield(long millis) {
		if (millis < 0)
			throw new IllegalArgumentException();
		try {
			Thread.sleep(millis);
			// TODO: this may also be a good place to introduce interleaving.
		} catch (InterruptedException e) {
			e.printStackTrace();
			// TODO: What should be the semantics here?
		}
	}

	/**
	 * Causes the current capsule to disconnect from its parent. On
	 * disconnecting from all its parents, a terminate call is made to shutdown
	 * the capsule running thread. This is part of automatic garbage collection
	 * of capsules.
	 */
	public final synchronized void shutdown() {
	    /*
		panini$ref$count--;
		if (panini$ref$count == 0)
			panini$push(new org.paninij.runtime.types.Panini$Duck$Void(
					PANINI$SHUTDOWN));
		*/
	    throw new UnsupportedOperationException();
	}

	/**
	 * Causes the current capsule to immediately cease execution.
	 * 
	 * Shutdown is allowed only if the client capsule has permission to modify
	 * this capsule.
	 * 
	 * If there is a security manager, its checkAccess method is called with
	 * this capsule as its argument. This may result in throwing a
	 * SecurityException.
	 * 
	 * @throws SecurityException
	 *             - if the client capsule is not allowed to access this
	 *             capsule.
	 * 
	 */
	public final void exit() {
	    /*
		this.checkAccess();
		org.paninij.runtime.types.Panini$Duck$Void d = new org.paninij.runtime.types.Panini$Duck$Void(
				PANINI$EXIT);
		panini$push(d);
		*/
	    throw new UnsupportedOperationException();
	}

	/**
	 * Pushes a single object on this capsule's queue.
	 * 
	 * @param o
	 *            - Object to be stored.
	 */
	public final synchronized void panini$push(Object o) {
		panini$ensureSpace(1);
		panini$capsule$size = panini$capsule$size + 1;
		panini$capsule$objects[panini$capsule$tail++] = o;
		if (panini$capsule$tail >= panini$capsule$objects.length)
			panini$capsule$tail = 0;
		if (panini$capsule$size == 1)
			notifyAll();
	}

	/**
	 * Pushes two objects on this capsule's queue.
	 * 
	 * @param o1
	 *            - first object to be stored.
	 * @param o2
	 *            - second object to be stored.
	 */
	protected final synchronized void panini$push(Object o1, Object o2) {
		panini$ensureSpace(2);
		panini$capsule$size = panini$capsule$size + 2;
		panini$capsule$objects[panini$capsule$tail++] = o1;
		if (panini$capsule$tail >= panini$capsule$objects.length)
			panini$capsule$tail = 0;
		panini$capsule$objects[panini$capsule$tail++] = o2;
		if (panini$capsule$tail >= panini$capsule$objects.length)
			panini$capsule$tail = 0;
		if (panini$capsule$size == 2)
			notifyAll();
	}

	/**
	 * Pushes three objects on this capsule's queue.
	 * 
	 * @param o1
	 *            - first object to be stored.
	 * @param o2
	 *            - second object to be stored.
	 * @param o3
	 *            - third object to be stored.
	 */
	protected final synchronized void panini$push(Object o1, Object o2,
			Object o3) {
		panini$ensureSpace(3);
		panini$capsule$size = panini$capsule$size + 3;
		panini$capsule$objects[panini$capsule$tail++] = o1;
		if (panini$capsule$tail >= panini$capsule$objects.length)
			panini$capsule$tail = 0;
		panini$capsule$objects[panini$capsule$tail++] = o2;
		if (panini$capsule$tail >= panini$capsule$objects.length)
			panini$capsule$tail = 0;
		panini$capsule$objects[panini$capsule$tail++] = o3;
		if (panini$capsule$tail >= panini$capsule$objects.length)
			panini$capsule$tail = 0;
		if (panini$capsule$size == 3)
			notifyAll();
	}

	/**
	 * Pushes multiple objects on this capsule's queue.
	 * 
	 * @param items
	 *            - list of objects to be stored.
	 */
	protected final synchronized void panini$push(Object... items) {
		int numItems = items.length;
		panini$ensureSpace(numItems);
		panini$capsule$size = panini$capsule$size + numItems;
		for (Object o : items) {
			panini$capsule$objects[panini$capsule$tail++] = o;
			if (panini$capsule$tail >= panini$capsule$objects.length)
				panini$capsule$tail = 0;
		}
		if (panini$capsule$size == numItems)
			notifyAll();
	}

	/**
	 * Initialize the 'internal' system in a capsule.
	 * <p>
	 * Must be called <em>BEFORE</em> {@link #panini$capsule$init()}.
	 */
	public void panini$wire$sys() {
	}

	protected void panini$capsule$init() {
	}

}
