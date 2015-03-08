package org.paninij.apt;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.Source;

public class MakeDuck$Thread extends MakeDuck
{

    public static MakeDuck$Thread make(PaniniPress context) 
    {
        MakeDuck$Thread m = new MakeDuck$Thread();
        m.context = context;
        return m;
    }
    
    @Override
    public void makeSourceFile(DuckShape currentDuck)
    {
        try {
            context.createJavaFile(this.buildQualifiedClassName(currentDuck),
                                   buildDuck(currentDuck));
        } catch (UnsupportedOperationException ex) {
            context.warning(ex.toString());
        }
    }

    @Override
    String buildNormalDuck(DuckShape currentDuck)
    {
        currentDuck.returnType.asElement().getEnclosedElements();
        String src = Source.lines(0, 
                "package org.paninij.runtime.ducks;",
                "",
                "import org.paninij.runtime.ProcInvocation;",
                "import org.paninij.runtime.ResolvableFuture;",
                "#0",
                "import #1;",
                "",
                "public class #2 extends #4 implements ProcInvocation, ResolvableFuture<#4> {",
                "    public final int panini$procID;",
                "    private #4 panini$result = null;",
                "    boolean panini$isResolved = false;",
                "",
                "#5",
                "",
                "#3",
                "",
                "    @Override",
                "    public int panini$procID() {",
                "        return panini$procID;",
                "    }",
                "",
                "    @Override",
                "    public void panini$resolve(#4 result) {",
                "        synchronized (this) {",
                "            panini$result = result;",
                "            panini$isResolved = true;",
                "            this.notifyAll();",
                "        }",
                "#6",
                "    }",
                "",
                "    @Override",
                "    public #4 panini$get() {",
                "        while (panini$isResolved == false) {",
                "            try {",
                "                synchronized (this) {",
                "                    while (panini$isResolved == false) this.wait();",
                "                }",
                "            } catch (InterruptedException e) { /* try waiting again */ }",
                "         }",
                "         return panini$result;",
                "    }",
                "    /* The following override the methods of `#4` */",
                "#7",
                "}");
        return Source.format(src, this.buildParameterImports(currentDuck),
                                  currentDuck.getQualifiedReturnType(),
                                  this.buildClassName(currentDuck),
                                  this.buildConstructor(currentDuck),
                                  currentDuck.getSimpleReturnType(),
                                  this.buildParameterFields(currentDuck),
                                  this.buildReleaseArgs(currentDuck),
                                  this.buildFacades(currentDuck));
    }

    @Override
    String buildVoidDuck(DuckShape currentDuck)
    {
        String src = Source.lines(0, "package org.paninij.runtime.ducks;",
                                     "",
                                     "import org.paninij.runtime.ProcInvocation;",
                                     "#0",
                                     "",
                                     "public class #1 implements ProcInvocation {",
                                     "    public final int panini$procID;",
                                     "#2",
                                     "",
                                     "#3",
                                     "",
                                     "    @Override",
                                     "    public int panini$procID() {",
                                     "        return panini$procID;",
                                     "    }",
                                     "}");
        
        return Source.format(src, this.buildParameterImports(currentDuck), 
                                  this.buildClassName(currentDuck), 
                                  this.buildParameterFields(currentDuck),
                                  this.buildConstructor(currentDuck));
    }

    

    @Override
    String buildClassName(DuckShape currentDuck)
    {
        return currentDuck.toString() + "$Thread";
    }

    @Override
    String buildQualifiedClassName(DuckShape currentDuck)
    {
        return packageName + "." + currentDuck.toString() + "$Thread";
    }

    @Override
    String buildConstructor(DuckShape currentDuck)
    {
       String constructor = buildConstructorDecl(currentDuck);
       constructor += "        panini$procID = procID;\n";
       
       for(int i = 0; i < currentDuck.parameters.size(); i++)
       {
           constructor += "        panini$arg" + i + " = arg" + i +";\n";
       }
       
       return constructor + "    }";
    }

    @Override
    String buildConstructorDecl(DuckShape currentDuck)
    {
        String constructorDecl = "    public " + buildClassName(currentDuck) + "(int procID";
        
        for(int i = 0; i < currentDuck.parameters.size(); i++)
        {
            constructorDecl += ", " + Source.dropPackageName(currentDuck.parameters.get(i)) + " arg" + i;
        }
        
        constructorDecl += ") {\n";
        
        return constructorDecl;
    }
    
    

    
    

}
