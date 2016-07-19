VTMHardwareSetup : VTMContextManager {

	//a hardware setups parent will be an Application
	*new{arg network, declaration, defintion;
		^super.new('hardware', network, declaration, defintion).initHardwareSetup;
	}

	initHardwareSetup{
		//Load hardware setup script/definition, or similar.
		//Hosts arbitrary number of HardwareDevice contexts
	}

	devices{ ^children; }
	application{ ^this.network.application; }
	network{ ^this.parent; }
}