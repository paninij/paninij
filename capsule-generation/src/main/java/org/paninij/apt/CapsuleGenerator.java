package org.paninij.apt;

import org.paninij.model.Capsule;

public class CapsuleGenerator {

    public static void generate(PaniniProcessor context, Capsule capsule) {
        CapsuleGenerator generator = new CapsuleGenerator();
        generator.generateCapsule(capsule);
    }

    private void generateCapsule(Capsule capsule) {
        System.out.println("# " + capsule.getQualifiedName());
        // TODO
        // delegate to classes designated by Execution profile
    }

}
