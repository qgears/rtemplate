package hu.qgears.rtemplate.runtime;

public class DummyCodeGeneratorContext implements ICodeGeneratorContext {

	@Override
	public boolean needReport() {
		return false;
	}

	@Override
	public void createFile(String path, String o) {
	}

	@Override
	public void createReport(String path, String o, TemplateTracker tt) {
	}

}
