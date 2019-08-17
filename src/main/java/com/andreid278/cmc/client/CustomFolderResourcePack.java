package com.andreid278.cmc.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nullable;

import com.google.common.base.CharMatcher;

import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.ResourcePackFileNotFoundException;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.Util;

public class CustomFolderResourcePack extends FolderResourcePack {
	private static final boolean ON_WINDOWS = Util.getOSType() == Util.EnumOS.WINDOWS;
    private static final CharMatcher BACKSLASH_MATCHER = CharMatcher.is('\\');
    
	public CustomFolderResourcePack(File resourcePackFileIn) {
		super(resourcePackFileIn);
	}
	
	@Override
	public <T extends IMetadataSection> T getPackMetadata(MetadataSerializer metadataSerializer, String metadataSectionName) throws IOException {
		return null;
	}
	
	protected static boolean validatePath(File p_191384_0_, String p_191384_1_) throws IOException
    {
        String s = p_191384_0_.getCanonicalPath();

        if (ON_WINDOWS)
        {
            s = BACKSLASH_MATCHER.replaceFrom(s, '/');
        }
        
        s = s.toLowerCase();

        return s.endsWith(p_191384_1_.toLowerCase());
    }
	
	@Override
	protected InputStream getInputStreamByName(String name) throws IOException
    {
        File file1 = this.getFile(name);

        if (file1 == null)
        {
            throw new ResourcePackFileNotFoundException(this.resourcePackFile, name);
        }
        else
        {
            return new BufferedInputStream(new FileInputStream(file1));
        }
    }
	
	@Override
	protected boolean hasResourceName(String name)
    {
        return this.getFile(name) != null;
    }

    @Nullable
    private File getFile(String p_191385_1_)
    {
        try
        {
            File file1 = new File(this.resourcePackFile, p_191385_1_);

            if (file1.isFile() && validatePath(file1, p_191385_1_))
            {
                return file1;
            }
        }
        catch (IOException var3)
        {
            ;
        }

        return null;
    }
}
