package com.m2u.eyelink.util;

import java.util.Enumeration;

public class DelegateEnumeration<E> implements Enumeration<E> {
	   private static final Object NULL_OBJECT = new Object();

	    private final Enumeration<E> delegate;
	    private final Filter<E> filter;

	    private boolean hasMoreElements;
	    private E nextElement;
	    private Exception nextException;


	    private static final Filter SKIP_FILTER = new Filter() {
	        @Override
	        public boolean filter(Object o) {
	            return false;
	        }
	    };

	    @SuppressWarnings("unchecked")
	    public DelegateEnumeration(Enumeration<E> delegate) {
	        this(delegate, SKIP_FILTER);
	    }

	    public DelegateEnumeration(Enumeration<E> delegate, Filter<E> filter) {
	        this.delegate = delegate;
	        this.filter = filter;
	    }

	    @Override
	    public boolean hasMoreElements() {
	        next(true);
	        return hasMoreElements;
	    }

	    @Override
	    public E nextElement() {
	        next(false);
	        // nextException
	        if (nextException != null) {
	            final Exception exception = this.nextException;
	            clearNext();
	            this.<RuntimeException>throwException(exception);
	        }
	        // nextResult
	        final E result = getNextElement();
	        clearNext();
	        return result;
	    }

	    private void clearNext() {
	        this.nextException = null;
	        this.nextElement = null;
	    }

	    private E getNextElement() {
	        if (nextElement == NULL_OBJECT) {
	            return null;
	        }
	        return nextElement;
	    }

	    @SuppressWarnings("unchecked")
	    private <T extends Exception> void throwException(final Exception exception) throws T {
	        throw (T) exception;
	    }


	    private void next(final boolean hasMoreElementMethod) {
	        if (nextElement != null || nextException != null) {
	            return;
	        }

	        while (true) {
	            final boolean nextExist = delegate.hasMoreElements();
	            if (!nextExist && hasMoreElementMethod) {
	                this.hasMoreElements = false;
	                return;
	            }
	            // error emulation
	            E nextElement;
	            try {
	                nextElement = delegate.nextElement();
	            } catch (Exception e) {
	                this.hasMoreElements = nextExist;
	                this.nextException = e;
	                break;
	            }

	            if (filter.filter(nextElement)) {
	                continue;
	            }

	            this.hasMoreElements = nextExist;
	            if (nextElement == null) {
	                this.nextElement = (E) NULL_OBJECT;
	            } else {
	                this.nextElement = nextElement;
	            }
	            break;

	        }

	    }

	    // for Test
	    Exception _getNextException() {
	        return nextException;
	    }

	    public interface Filter<E> {
	        boolean filter(E e);
	    }
}
