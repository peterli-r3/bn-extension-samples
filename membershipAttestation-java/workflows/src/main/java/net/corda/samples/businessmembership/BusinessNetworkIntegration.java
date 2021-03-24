package net.corda.samples.businessmembership;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.bn.flows.BNService;
import net.corda.bn.flows.IllegalMembershipStatusException;
import net.corda.bn.flows.MembershipAuthorisationException;
import net.corda.bn.flows.MembershipNotFoundException;
import net.corda.bn.states.BNRole;
import net.corda.bn.states.MembershipState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.samples.businessmembership.states.InsurerIdentity;
import net.corda.samples.businessmembership.states.CareProviderIdentity;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * This abstract flow is extended by any flow which will use business network membership verification methods.
 */
public abstract class BusinessNetworkIntegration extends FlowLogic<SignedTransaction> {

    @Suspendable
    protected Memberships businessNetworkPartialVerification(String networkId, Party insurer, Party careProvider) throws MembershipNotFoundException {
        BNService bnService = getServiceHub().cordaService(BNService.class);

        StateAndRef<MembershipState> insurerMembership = null;
        StateAndRef<MembershipState> careProMembership = null;

        try {
             insurerMembership = bnService.getMembership(networkId,insurer);
        }catch(Exception e){
            throw new MembershipNotFoundException("insurer is not part of Business Network with $networkId ID");
        }
        try {
            careProMembership = bnService.getMembership(networkId,insurer);
        }catch(Exception e){
            throw new MembershipNotFoundException("insurer is not part of Business Network with $networkId ID");
        }
        return new Memberships(insurerMembership, careProMembership);
    }


    /**
     * Verifies that [lender] and [borrower] are members of Business Network with [networkId] ID, their memberships are active, contain
     * business identity of [BankIdentity] type and that lender is authorised to issue the loan.
     */
    @Suspendable
    protected void businessNetworkFullVerification(String networkId, Party policyIssuer, Party careProvider) throws MembershipNotFoundException {
        BNService bnService = getServiceHub().cordaService(BNService.class);
        try{
            MembershipState policyIssuerMembership = bnService.getMembership(networkId,policyIssuer).getState().getData();
            if (!policyIssuerMembership.isActive()){
                throw new IllegalMembershipStatusException("$policyIssuer is not active member of Business Network with $networkId ID");
            }
            if(policyIssuerMembership.getIdentity().getBusinessIdentity().getClass()!= InsurerIdentity.class){
                throw new IllegalMembershipBusinessIdentityException("$policyIssuer business identity should be InsurerIdentity");
            }
            Set<BNRole> setRoles = policyIssuerMembership.getRoles();
            for (BNRole role : setRoles){
                if(!role.getPermissions().contains(InsurerIdentity.IssuePermissions.CAN_ISSUE_POLICY)){
                    throw new MembershipAuthorisationException("$policyIssuer is not authorised to issue insurance Polict in Business Network with $networkId ID");
                }
            }
        } catch (Exception e){
            throw new MembershipNotFoundException("$policyIssuer is not member of Business Network with $networkId ID");
        }
        try{
            MembershipState careProviderMembership = bnService.getMembership(networkId,careProvider).getState().getData();
            if (!careProviderMembership.isActive()){
                throw new IllegalMembershipStatusException("$policyIssuer is not active member of Business Network with $networkId ID");
            }
            if(careProviderMembership.getIdentity().getBusinessIdentity().getClass()!= CareProviderIdentity.class){
                throw new IllegalMembershipBusinessIdentityException("$policyIssuer business identity should be InsurerIdentity");
            }
        } catch (Exception e){
            throw new MembershipNotFoundException("$policyIssuer is not member of Business Network with $networkId ID");
        }
    }





    public class Memberships{
        private StateAndRef<MembershipState> MembershipA;
        private StateAndRef<MembershipState> MembershipB;

        public Memberships(StateAndRef<MembershipState> membershipA, StateAndRef<MembershipState> membershipB) {
            MembershipA = membershipA;
            MembershipB = membershipB;
        }

        public StateAndRef<MembershipState> getMembershipA() {
            return MembershipA;
        }

        public StateAndRef<MembershipState> getMembershipB() {
            return MembershipB;
        }
    }

    public class IllegalMembershipBusinessIdentityException extends FlowException{
        public IllegalMembershipBusinessIdentityException(@Nullable String message) {
            super(message);
        }
    }

}
