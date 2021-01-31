package edu.uw.cs.listeners;

import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.AutomaticBean;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

/**
 * A verbose logger for text output related to CSE 14x at UW.
 * This may not be the optimal version of this class and we would eventually
 * like to migrate this listener to an override of the DefaultLogger class
 * but for now this is good enough.
 * @see DefaultLogger
 */
public class VerboseListener extends AutomaticBean implements AuditListener {
  /**
   * The total number of errors raised during the audit of the current project/file.
   */
  private int totalErrorCount = 0;

  @Override
  public void auditFinished(AuditEvent auditEvent) {
    System.out.println();
    if (totalErrorCount == 0) {
      System.out.println("No style errors found!");
    } else if (totalErrorCount == 1) {
      System.out.println("Found " + totalErrorCount + " error.");
    } else {
      System.out.println("Found " + totalErrorCount + " errors.");
    }
    printDisclaimer();
    System.out.flush();
  }

  @Override
  public void addError(AuditEvent auditEvent) {
    logError(auditEvent);
    totalErrorCount++;
  }

  @Override
  public void addException(AuditEvent auditEvent, Throwable aThrowable) {
    logError(auditEvent);
    aThrowable.printStackTrace(System.out);
    totalErrorCount++;
  }

  @Override
  protected void finishLocalSetup() throws CheckstyleException {
    // No code by default
  }

  @Override
  public void fileStarted(AuditEvent auditEvent) {
    // Say nothing when the file is starting to be processed
  }

  @Override
  public void fileFinished(AuditEvent auditEvent) {
    // Say nothing when the file is finished being processed
  }

  @Override
  public void auditStarted(AuditEvent auditEvent) {
    // Say nothing when the audit starts
  }

  /**
   * Logs the given audit event to the console.
   * @param auditEvent the audit event that was raised by the checker
   */
  private void logError(AuditEvent auditEvent) {
    System.out.println("Error found on" 
        + " line " + auditEvent.getLine() + ","
        + " column " + auditEvent.getColumn() + ": "
        + auditEvent.getMessage());
  }

  /**
   * Prints a disclaimer about the risks associated with using this tool to the console.
   * 
   * Specifically, we state that this tool does not garuntee a student gets a certain score
   * on the style portion of their homework based on the output of this tool.
   */
  private void printDisclaimer() {
    System.out.println();
    System.out.println("NOTE: Use this tool at your own risk. We do not guarantee that using this");
    System.out.println("tool will prevent you from losing style points on an assignment. Getting");
    System.out.println("\"no errors found\" does not guarantee that you will earn any particular");
    System.out.println("grade on an assignment.");
    System.out.println();
  }
}