package org.nasdanika.diagramgenerator.plantuml;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;

import org.nasdanika.common.DiagramGenerator;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUMLDiagramGenerator implements DiagramGenerator {
	
	@Override
	public boolean isSupported(String dialect) {
		return DiagramGenerator.UML_DIALECT.equals(dialect)
				|| DiagramGenerator.GANTT_DIALECT.equals(dialect)
				|| DiagramGenerator.MINDMAP_DIALECT.equals(dialect)
				|| DiagramGenerator.SALT_DIALECT.equals(dialect)
				|| DiagramGenerator.WBS_DIALECT.equals(dialect);
	}
	
	@Override
	public String generateDiagram(String spec, String dialect) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			StringBuilder sb = new StringBuilder("@start")
					.append(dialect)
					.append(System.lineSeparator())
					.append(spec)
					.append(System.lineSeparator())
					.append("@end")
					.append(dialect)
					.append(System.lineSeparator());
			
			SourceStringReader reader = new SourceStringReader(sb.toString());
			
			FileFormatOption fileFormatOption = new FileFormatOption(FileFormat.PNG);
			reader.outputImage(baos, 0, fileFormatOption);		
			String diagramCMap = reader.getCMapData(0, fileFormatOption);
			baos.close();

			StringBuilder ret = new StringBuilder("<img src=\"data:image/png;base64, ");
			ret
				.append(Base64.getEncoder().encodeToString(baos.toByteArray()))
				.append("\"");
			
			if (org.nasdanika.common.Util.isBlank(diagramCMap)) {
				ret.append("/>");
				return ret.toString();			
			}
			
			String openingTag = "<map id=\"plantuml_map\" name=\"plantuml_map\">";
			if (diagramCMap.startsWith(openingTag)) {
				String mapId = "plantuml_map_" + UUID.randomUUID().toString();
				ret			
				.append(" usemap=\"#")
				.append(mapId)
				.append("\"/>")
				.append(System.lineSeparator())
				.append("<map id=\"")
				.append(mapId)
				.append("\" name=\"")
				.append(mapId)
				.append("\">")
				.append(diagramCMap.substring(openingTag.length()));
				
			} else {				
				ret			
					.append(" usemap=\"#plant_uml_map\"/>")
					.append(System.lineSeparator())
					.append(diagramCMap);
				return ret.toString();
			}
									
			return ret.toString();			
		} catch (Exception e) {
			return "<div class=\"nsd-error\">Error during diagram rendering: " + e + "</div>";
		}
	}

}
