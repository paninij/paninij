package org.paninij.proc.check.signature;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.util.PaniniModel;
import org.paninij.proc.util.Source;

public class SuffixCheck implements SignatureCheck
{
    @Override
    public Result checkSignature(TypeElement signature)
    {
        String name = signature.getSimpleName().toString();
        String err = null;
        if (! name.endsWith(PaniniModel.SIGNATURE_TEMPLATE_SUFFIX))
        {
            err = Source.cat("Invalid signature name: `#0`",
                             "Every signature template name must be suffixed with `#1`");
            err = Source.format(err, name, PaniniModel.SIGNATURE_TEMPLATE_SUFFIX);
        }
        else if (name.length() == PaniniModel.SIGNATURE_TEMPLATE_SUFFIX.length())
        {
            err = Source.cat("Invalid signature name: `#0`",
                             "Signature name can't be the same as the expected suffix.");
            err = Source.format(err, name);
        }

        return (err == null) ? Result.ok : new Result.Error(err, SuffixCheck.class, signature);
    }
}
