package me.ruslanys.vkaudiosaver.component;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public interface FileDownloader extends Callable<File> {

}
