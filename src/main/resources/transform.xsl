<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:key name="typeKey" match="ConcertNumber" use="@type"/>
    <xsl:template match="/Dance">
        <GroupedDances>
            <xsl:for-each select="ConcertNumber[generate-id() = generate-id(key('typeKey', @type)[1])]">
                <Group type="{@type}">
                    <xsl:for-each select="key('typeKey', @type)">
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </Group>
            </xsl:for-each>
        </GroupedDances>
    </xsl:template>
</xsl:stylesheet>