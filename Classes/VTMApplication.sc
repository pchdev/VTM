VTMApplication {
	var <network;
	var <sceneOwner;
	var <moduleHost;
	var <hardwareSetup;
	var <filePaths;
	var <declaration;
	var <definition;

	var oscResponders;

	//The network[declaration\definition] is admittedly strange here, but keeping it for now.
	*new{arg name, declaration, definition;
		^super.new.initApplication(name, declaration, definition);
	}

	initApplication{arg name_, declaration_, definition_;
		var networkDesc, networkDef;
		var moduleDesc, moduleDef;
		var sceneDesc, sceneDef;
		var hardwareDesc, hardwareDef;
		if(declaration.notNil, {
			if(declaration.includesKey(\network), {
				networkDesc = declaration[\network][\declaration];
				networkDef = declaration[\network][\definition];
			});
			if(declaration.includesKey(\network), {
				moduleDesc = declaration[\module][\declaration];
				moduleDef = declaration[\module][\definition];
			});
			if(declaration.includesKey(\network), {
				sceneDesc = declaration[\scene][\declaration];
				sceneDef = declaration[\scene][\definition];
			});
			if(declaration.includesKey(\network), {
				hardwareDesc = declaration[\hardware][\declaration];
				hardwareDef = declaration[\hardware][\definition];
			});
		});
		this.prInitFilePaths;
		declaration = declaration_;
		definition = definition_;
		network = VTMNetwork(name_, this, networkDesc, networkDef);
		moduleHost = VTMModuleHost(network, moduleDesc, moduleDef);
		sceneOwner = VTMSceneOwner(network, sceneDesc, sceneDef);
		hardwareSetup = VTMHardwareSetup(network, hardwareDesc, hardwareDef);

		this.makeOSCResponders;

		//Discover other application on the network
		network.discover;
		if(declaration.notNil, {
			if(declaration.includeKey(\openView), {
				if(declaration[\openView], {
					var viewDesc, viewDef;
					this.makeView(
						viewDeclaration: declaration[\viewDeclaration],
						viewDefinition: declaration[\viewDefinition]
					);
				});
			});
		});
	}

	prInitFilePaths{
		filePaths = IdentityDictionary.new;
		filePaths[\vtm] = PathName(
			PathName(this.class.filenameSymbol.asString).parentPath
		).parentPath;
		filePaths[\moduleDefintions] = filePaths[\vtm] +/+ "ModuleDefintions";
		filePaths[\hardwareDefinitions] = filePaths[\vtm] +/+ "HardwareDefinitions";
	}

	addPath{arg type, path;

	}

	makeOSCResponders{
	}

	runHardwareSetupScript{arg path;
		hardwareSetup.addHardware(path);//mock code
	}

	getFilePathFor{arg key;
		^filePaths[key];
	}

	name{
		^network.name;
	}

	makeView{arg parent, bounds, viewDeclaration, viewDefinition;
		^VTMApplicationView.new(
			parent, bounds, this, viewDeclaration, viewDefinition
		);
	}
}