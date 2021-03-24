# Membership Attestation

//Step1: Create the network
flow start CreateNetwork

//Step2: 2 non-member makes the request to join the network.
flow start RequestMembership authorisedParty: NetworkOperator, networkId: 9ccbbdcc-8b69-4c93-a559-50e801ecd7f8

//Step3: go back to the admin node, and query all the membership requests.
flow start QueryAllMembers

//Step4: Admin active membership, two times, ONLY the membership activation
Insurance:
flow start ActiveMembers membershipId: 85e41e8f-11d6-42b0-92d0-71e9e6ebafa7

CarePro:
flow start ActiveMembers membershipId: 5d8ee286-a588-43a0-b82f-bf76e7528211

//Step5: Admin create subgroup and add group members.
flow start CreateNetworkSubGroup networkId: 9ccbbdcc-8b69-4c93-a559-50e801ecd7f8, groupName: APAC_Insurance_Alliance, groupParticipants: [57062f29-a294-44e1-8e43-7d9ce3e31892, 85e41e8f-11d6-42b0-92d0-71e9e6ebafa7, 5d8ee286-a588-43a0-b82f-bf76e7528211]

//Step6: Admin assign business identity to a member.
flow start AssignBNIdentity firmType: InsuranceFirm, membershipId: 85e41e8f-11d6-42b0-92d0-71e9e6ebafa7, bnIdentity: APACIN76CZX

//Step7: Admin assign business identity related ROLE to the member.
flow start AssignPolicyIssuerRole membershipId: 85e41e8f-11d6-42b0-92d0-71e9e6ebafa7, networkId: 9ccbbdcc-8b69-4c93-a559-50e801ecd7f8

//Step8: Admin assign business identity to the second member
flow start AssignBNIdentity firmType: CareProvider, membershipId: 5d8ee286-a588-43a0-b82f-bf76e7528211, bnIdentity: APACCP44OJS

//Query to check
run vaultQuery contractStateType: net.corda.core.contracts.ContractState

-------------------Network setup is done, and business flow begins--------------------------

/* Step9: The insurance Company will issue a policy to insuree.
* The flow initiator (the insurance company) has to be a member of the Business network, has to have a insuranceIdentity, and has to have issuer Role, and has to have issuance permission.
  */
  flow start IssuePolicy networkId: 9ccbbdcc-8b69-4c93-a559-50e801ecd7f8, careProvider: CarePro, insuree: PeterLi

//Step10: Query the state in the CarePro node.
run vaultQuery contractStateType: net.corda.samples.businessmembership.states.InsuranceState



//flow start AssignBNIdentity firmType: CareProvider, membershipId: 9cc6abf7-e20e-4404-be8c-4bcec1a668e0, bnIdentity: APACCP44OJS
