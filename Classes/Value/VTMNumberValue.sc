VTMNumberValue : VTMValue {
	var <dataspace;//Optional instance of VTMDataspace
	var <scheduler;//Where instances of VTMNumberInterpolator will be

	*new{arg properties;
		^super.new(properties).initNumberValue;
	}

	isValidType{arg val;
		^val.isKindOf(SimpleNumber);
	}

	isValidValue{arg val;
		^true; //TEMP implement checks in subclasses
	}

	initNumberValue{
		if(properties.notEmpty, {
			if(properties.includesKey(\clipmode), {
				this.clipmode = properties[\clipmode];
			});
			if(properties.includesKey(\minVal), {
				this.minVal = properties[\minVal];
			});
			if(properties.includesKey(\maxVal), {
				this.maxVal = properties[\maxVal];
			});
			if(properties.includesKey(\stepsize), {
				this.stepsize = properties[\stepsize];
			});
		});
	}



	ramp{arg targetValue, time, curve = \lin;
		if(scheduler.isPlaying, {
			scheduler.stop;
		});
		scheduler = fork{
			var stream, val;
			stream = Env([this.value, targetValue], [time]).asPseg.asStream;
			val = stream.next;
			loop{
				if(val.isNil, {
					this.valueAction_(targetValue);
					thisThread.stop;
				});
				this.valueAction_(val);
				0.05.wait;
				val = stream.next;
			};
		};
	}


	defaultValue_{arg val;
		super.defaultValue = this.prCheckRangeAndClipValue(val);
	}

	increment{arg doAction = true;
		if(doAction, {
			this.valueAction_(this.value + this.stepsize);
		}, {
			this.value_(this.value + this.stepsize);
		});
	}

	decrement{ arg doAction = true;
		if(doAction, {
			this.valueAction_(this.value - this.stepsize);
		}, {
			this.value_(this.value - this.stepsize);
		});
	}

	*propertyKeys{
		^(super.propertyKeys ++ [\minVal, \maxVal, \stepsize, \clipmode/*, \dataspace*/]);
	}


	prCheckRangeAndClipValue{arg val;
		var result;
		result = val;
		switch(this.clipmode,
			\none, {
				// "NONE CLIPPING".postln;
			},
			\low, {
				// "LOW CLIPPING".postln;
				if(this.minVal.notNil and: {val < this.minVal}, {
					result = val.max(this.minVal);
				});
			},
			\high, {
				// "HIGH CLIPPING".postln;
				if(this.maxVal.notNil and: {val > this.maxVal}, {
					result = val.min(this.maxVal);
				});
			},
			\both, {
				// "BOTH CLIPPING".postln;
				if(this.minVal.notNil and: {val < this.minVal}, {
					result = val.max(this.minVal);
				}, {
					if(this.maxVal.notNil and: {val > this.maxVal}, {
						result = val.min(this.maxVal);
					});
				});
			}
		);
		^result;
	}

	*defaultViewType{ ^\slider; }

	//Properties setters and getters
	minVal_{ arg val;
		if(val.isNil, {
			this.set(\minVal, nil);
		}, {
			if(this.isValidType(val), {
				this.set(\minVal, val);
				this.value_(this.value);//update the value, might be clipped in the value set method
			}, {
				"NumberValue:minVal_ - ignoring val because of invalid type: '%[%]'".format(
					val, val.class
				).warn;
			});
		});
	}
	minVal{ ^this.get(\minVal); }

	maxVal_{ arg val;
		if(val.isNil, {
			this.set(\maxVal, nil);
		}, {
			if(this.isValidType(val), {
				this.set(\maxVal, val);
				this.value_(this.value);//update the value, might be clipped in the value set method
			}, {
				"NumberValue:maxVal_ - ignoring val because of invalid type: '%[%]'".format(
					val, val.class
				).warn;
			});

		});
	}
	maxVal{ ^this.get(\maxVal); }

	stepsize_{ arg val;
		var newVal = val;
		if(this.isValidType(val), {
			if(newVal.isNegative, {
				newVal = newVal.abs;
				"NumberValue:stepsize_ - val converted to positive value".warn;
			});
			this.set(\stepsize, newVal);
		}, {
			"NumberValue:stepsize_ - ignoring val because of invalid type: '%[%]'".format(
				val, val.class
			).warn;
		});
	}
	stepsize{ ^this.get(\stepsize) ? 0; }

	clipmode_{ arg val;
		if(#['none', 'low', 'high', 'both'].includes(val.asSymbol), {
			var newVal;
			this.set(\clipmode, val.asSymbol);
			this.value_(this.value);//update the value, might be clipped in the value set method
		}, {
			"NumberValue:clipmode_ - ignoring val because of invalid type: '%[%]'".format(
				val, val.class
			).warn;
		});
	}
	clipmode{ ^this.get(\clipmode) ? \none; }

	value_{arg val;
		super.value_(
			this.prCheckRangeAndClipValue(val)
		);
	}

}
