package com.campusmarket.service;

import com.campusmarket.dao.ListingImageDao;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ListingImageService {
    public static final long MAX_FILE_SIZE=3L*1024*1024;
    private final ListingImageDao dao=new ListingImageDao();

    public List<Part> selected(Collection<Part> parts) {
        List<Part> out=new ArrayList<>();
        for(Part p:parts) if(p.getName().startsWith("photo")&&p.getSize()>0) out.add(p);
        if(out.size()>3)throw new IllegalArgumentException("Upload no more than three pictures.");
        return out;
    }

    public void save(long listingId,List<Part> photos,ServletContext context) throws IOException {
        if(photos.isEmpty())throw new IllegalArgumentException("Add at least one clear product picture.");
        Path dir=Path.of(context.getRealPath("/img/uploads")).toAbsolutePath().normalize();
        Files.createDirectories(dir);List<String> saved=new ArrayList<>();
        try {
            for(Part photo:photos){
                byte[] bytes=photo.getInputStream().readAllBytes();String ext=extension(bytes);
                if(bytes.length==0||bytes.length>MAX_FILE_SIZE)throw new IllegalArgumentException("Each picture must be 3 MB or smaller.");
                String name=UUID.randomUUID()+ext;Path target=dir.resolve(name).normalize();
                if(!target.startsWith(dir))throw new IllegalArgumentException("Invalid image name.");
                Files.write(target,bytes,StandardOpenOption.CREATE_NEW);saved.add("uploads/"+name);
            }
            dao.replace(listingId,saved);
        }catch(Exception e){for(String path:saved)Files.deleteIfExists(dir.resolve(Path.of(path).getFileName()));if(e instanceof IOException io)throw io;throw (RuntimeException)e;}
    }

    private String extension(byte[] b) {
        if(b.length>=3&&(b[0]&255)==0xff&&(b[1]&255)==0xd8&&(b[2]&255)==0xff)return ".jpg";
        if(b.length>=8&&(b[0]&255)==0x89&&b[1]=='P'&&b[2]=='N'&&b[3]=='G')return ".png";
        if(b.length>=12&&b[0]=='R'&&b[1]=='I'&&b[2]=='F'&&b[3]=='F'&&b[8]=='W'&&b[9]=='E'&&b[10]=='B'&&b[11]=='P')return ".webp";
        throw new IllegalArgumentException("Pictures must be JPG, PNG or WebP files.");
    }
}
