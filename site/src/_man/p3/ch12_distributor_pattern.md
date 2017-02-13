---
part: 3
chapter: 12
title: Distributor Pattern
---

The Fibonacci example below computes the Fibonacci numbers via the collaboration
of a set of worker and distributor capsules. The worker capsules, of type
FibWorker, iteratively compute the Fibonacci numbers, whereas the distributor
capsule, of type Distributor, distributes the computation work among the worker
capsules.

**Listing 12.1:** *Fibonacci Example*
{% highlight java linenos %}
signature Worker {
    Number execute(int num);
}

capsule FibWorker (Worker w) implements Worker {
    Number execute(int n) {
        if (n < 2) return new Number(n);
        if (n < 13) return new Number(helper(n));
        return new Sum (w.execute(n-1), w.execute(n-2));
    }
    private int helper(int n) {
        int prev1=0, prev2=1;
        for(int i=0; i<n; i++) {
            int savePrev1 = prev1;
            prev1 = prev2;
            prev2 = savePrev1 + prev2;
        }
        return prev1;
    }
}

capsule Distributor (Worker[] workers) implements Worker {
    int current = 0;
    Number execute(int num) {
        Number result = workers[current].execute(num);
        current++;
        if(current == workers.length) current = 0;
        return result;
    }
}

capsule Fibonacci (String[] args) {
    design {
        FibWorker workers[10];
        Distributor d;
        d(workers);
        wireall(workers, d);
    }
    void run(){
        try {
            System.out.println(d.execute(Integer.parseInt(args[0])).v());
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage: panini Fibonacci <Number>");
        }
    }
}

class Number {
    int number;
    Number(int number){ this.number = number; }
    int v(){ return number;}
    public String toString() { return "" + number; }
}

class Sum extends Number {
    Number left; Number right;
    Sum(Number left, Number right){ super(0); this.left = left; this.right = right; }
    @Override int v() { return left.v()+right.v(); }
}
{% endhighlight %}

The system capsule of Fibonacci, lines 32–46, declares a system design with 10
Fibworker capsule instances, line 34, and 1 Distributor capsule instance,
line 35. Each worker capsule is connected to the distributor capsule, line 37, and
vice versa, line 36. The Fibonacci capsule also runs the program to compute the
Fibonacci number for the of the input value of args[0], line 41, by invoking the
execute method of the distributor capsule.

The interface for worker and distributor capsules is specified by the signature
Worker, lines 1–3, containing a method execute, implemented by both capsule
implementations. The FibWorker capsule implements the signature on lines 1–3. In
the worker capsule, (1) the Fibonacci number for numbers less than 2 is equal to
the number itself, line 7; (2) for Fibonacci numbers less than 13, the capsule
uses a local helper method, line 8, that iteratively computes the Fibonacci
number; and (3) finally for number greater than 13, the computation of the
Fibonacci number is sent to worker capsule w, line 9. According the the system
design, especially line 37, the worker capsule w is the distributor, which in
turn send the computation of the Fibonacci numbers to some other worker
capsules. The Distributer capsule, on lines 22–30, implements the signature
Worker. The distributor invokes execute on the first worker capsules it has
available to compute the Fibonacci number num. If the worker number is less than
13, the worker capsule computes the Fibonacci number and returns it. Otherwise,
the worker capsule invokes back the execute method of the distributor with
messages to compute the Fibonacci for numbers num-1 and num-2. The distributor
in turn, invokes other worker capsules to do these computations.

The classes Number, lines 48–53 and Sum, lines 55–58, are wrapper classes that
encapsulate numerical values and their additions.
