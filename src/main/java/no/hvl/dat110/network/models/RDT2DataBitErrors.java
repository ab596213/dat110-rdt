package no.hvl.dat110.network.models;

import no.hvl.dat110.network.Datagram;
import no.hvl.dat110.network.IChannelModel;
import no.hvl.dat110.transport.rdt2.SegmentRDT2;

public class RDT2DataBitErrors implements IChannelModel {

	private static double CORRUPTPB = 0.4;
	
	public int delay () {
		return 0;
	}
	
	public Datagram process (String name, Datagram datagram) {
		
		SegmentRDT2 segment = (SegmentRDT2) datagram.getSegment();
		
		if (segment.isData() && Math.random() < CORRUPTPB) {
			
			segment.setChecksum((byte)(~(segment.getChecksum()))); 
			
			if (segment.getSeqnr()==1) {
				segment.setSeqnr(0);
			} else {
				segment.setSeqnr(1);
			}
					
			
			System.out.println("[Network:" + name + "*   ] transmit: " + datagram.toString());

		} else {
			System.out.println("[Network:" + name + "+   ] transmit: " + datagram.toString());
		}
		
		return datagram;
			
	}
}
