package no.hvl.dat110.network.models;

import no.hvl.dat110.network.Datagram;
import no.hvl.dat110.network.IChannelModel;
import no.hvl.dat110.transport.rdt2.SegmentRDT2;
import no.hvl.dat110.transport.rdt21.SegmentRDT21;

public class RDT2DataAckNakBitErrors implements IChannelModel {

	private static double CORRUPTPB = 0.4;
	
	public int delay () {
		return 0;
	}
	
	public Datagram process (String name, Datagram datagram) {
		
		if (Math.random() < CORRUPTPB) {
			
			SegmentRDT2 segment = (SegmentRDT2) datagram.getSegment();
			
			segment.setChecksum((byte)(~(segment.getChecksum()))); 
						
			System.out.println("[Network:" + name + "*   ] transmit: " + datagram.toString());
			
		} else {
			
			System.out.println("[Network:" + name + "+   ] transmit: " + datagram.toString());
			
		}
		
		return datagram;
			
	}
}
