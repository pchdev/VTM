VTMParameterProxy : VTMContextProxy {

	*new{arg name, parent, declaration, definition;
		^super.new(name, parent, declaration, definition).initParameterProxy;
	}

	initParameterProxy{

	}
}