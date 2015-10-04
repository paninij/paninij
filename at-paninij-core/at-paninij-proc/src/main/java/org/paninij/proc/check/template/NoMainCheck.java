package org.paninij.proc.check.template;

import java.text.MessageFormat;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import org.paninij.proc.check.Result;

public class NoMainCheck implements TemplateCheck 
{
	public static String errorSource = NoMainCheck.class.getName();

	@Override
	public Result check(TypeElement template) 
	{
		for (Element elem : template.getEnclosedElements())
		{
			if(elem.getKind() == ElementKind.METHOD)
			{
				ExecutableElement execElem = (ExecutableElement) elem;
				if(execElem.getSimpleName().toString().compareTo("main") == 0)
				{
					Set<Modifier> mods = execElem.getModifiers();
					if(mods.contains(Modifier.PUBLIC) && mods.contains(Modifier.STATIC))
					{
						if(execElem.getReturnType().getKind() == TypeKind.VOID)
						{
							String err = "Capsule Templates must not contain a public static void main method."
									+ "A main method was found in `{0}`";
							err = MessageFormat.format(err, template.getSimpleName().toString());
							return new Result.Error(err, errorSource);
						}
					}
				}
			}
		}
		
		return Result.ok;
	}

}
