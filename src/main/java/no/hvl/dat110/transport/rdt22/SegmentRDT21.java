package no.hvl.dat110.transport.rdt22;

import no.hvl.dat110.transport.rdt2.SegmentRDT2;
import no.hvl.dat110.transport.rdt2.SegmentType;

public class SegmentRDT21 extends SegmentRDT2 {

	protected int seqnr;
	
	public SegmentRDT21() {
		
	}
	
	public SegmentRDT21(byte[] data, int seqnr) {
		super(data, seqnr);
		this.seqnr = seqnr;
	}
	
	public SegmentRDT21(SegmentType type) {
		super(type);
	}
	
	public SegmentRDT21(SegmentType type, int seqnr) {
		super(type);
		this.seqnr = seqnr;
	}
	
	public int getSeqnr () {
		return this.seqnr;
	}
	
	@Override
	public SegmentRDT21 clone() {

		SegmentRDT21 segment = new SegmentRDT21();

		if (this.data != null) {
			segment.data = this.data.clone();
		}

		segment.type = this.type;
		segment.checksum = this.checksum;
		segment.seqnr = this.seqnr;
		
		return segment;
	}
	
	public String toString() {
		
		String str = super.toString();
		
		if (type == SegmentType.DATA) {
			str = str + "[" + seqnr + "]";
		}
		
		return str;
	}
}
