package meghanada.analyze;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.EntryMessage;

public class ExpressionScope extends Scope {

  private static final Logger log = LogManager.getLogger(ExpressionScope.class);

  public AccessSymbol expressionReturn;
  public boolean isField;
  public Scope parent;

  public ExpressionScope() {}

  public ExpressionScope(final int pos, final Range range) {
    super(pos, range);
  }

  @Override
  public void addMethodCall(final MethodCall mcs) {
    assert mcs.arguments != null;
    final Integer endCol = super.range.end.column;
    final Integer endLine = super.range.end.line;
    final Position mcsEnd = mcs.range.end;
    if (mcsEnd.column + 1 == endCol && mcsEnd.line == endLine) {
      this.expressionReturn = mcs;
    }
    super.addMethodCall(mcs);
  }

  @Override
  public void dumpVariable() {
    final EntryMessage entryMessage = log.traceEntry("**** {} {}", this.getClassName(), this.range);
    super.dumpVariable(log);
    log.traceExit(entryMessage);
  }

  @Override
  public void dumpFieldAccess() {
    final EntryMessage entryMessage = log.traceEntry("**** {} {}", this.getClassName(), this.range);
    super.dumpFieldAccess(log);
    log.traceExit(entryMessage);
  }

  @Override
  public void dump() {
    final EntryMessage entryMessage = log.traceEntry("**** {} {}", this.getClassName(), this.range);
    super.dumpVariable(log);
    super.dumpFieldAccess(log);
    log.traceExit(entryMessage);
  }

  public Optional<AccessSymbol> getExpressionReturn() {
    log.traceEntry("expressionReturn={}", this.expressionReturn);
    final Optional<Variable> var = this.variables.stream().filter(Variable::isDecl).findFirst();

    if (var.isPresent()) {
      return log.traceExit(Optional.empty());
    }

    final Optional<AccessSymbol> aReturn = Optional.ofNullable(this.expressionReturn);
    return log.traceExit(aReturn);
  }

  @Override
  public void addVariable(final Variable variable) {
    assert variable.fqcn != null;
    if (isField) {
      variable.isField = true;
    }
    super.addVariable(variable);
  }
}
