package hu.qgears.rtemplate.runtime;

abstract public class RQuickTemplate extends RAbstractTemplatePart
{
	public RQuickTemplate() {
		super(new DummyCodeGeneratorContext());
	}
	public RQuickTemplate(RAbstractTemplatePart parent) {
		super(parent);
	}
	public String generate()
	{
		doGenerate();
		return templateState.out.toString();
	}
	public void generateVoid() {
		doGenerate();
	}

	abstract protected void doGenerate();
}
