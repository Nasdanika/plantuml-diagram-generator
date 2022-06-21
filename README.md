# plantuml-diagram-generator
Extract of PlantUML-dependent code from Core.

To use PlantUML dialects (UML, SALT, GANTT, MINDMAP, WBS) in your code use the code below to implement ``org.nasdanika.common.DiagramGenerator``.

## Pom dependency
```xml
		
		<dependency>
		    <groupId>net.sourceforge.plantuml</groupId>
		    <artifactId>plantuml</artifactId>
		    <version>1.2022.5</version>
		</dependency>		
```

## Java DiagramGenerator code processing PlantUML dialects

```java
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StringBuilder sb = new StringBuilder("@start")
				.append(dialect.name().toLowerCase())
				.append(System.lineSeparator())
				.append(spec)
				.append(System.lineSeparator())
				.append("@end")
				.append(dialect.name().toLowerCase())
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
		
		if (Util.isBlank(diagramCMap)) {
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
```
