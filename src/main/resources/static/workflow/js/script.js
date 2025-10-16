document.addEventListener('DOMContentLoaded', function () {
    // DOM element references
    const tenantForm = document.getElementById('tenantForm');
    const tenantIdInput = document.getElementById('tenantId');
    const proceedBtn = document.getElementById('proceedBtn');
    const formLoader = document.getElementById('formLoader');
    const authModal = document.getElementById('authModal');
    const mainContent = document.getElementById('mainContent');
    const workflowList = document.getElementById('workflowList');
    const emptyState = document.getElementById('emptyState');
    const newTemplateBtn = document.getElementById('newTemplateBtn');
    // Replace any existing event listener for createFirstTemplateBtn
    document.getElementById('createFirstTemplateBtn').addEventListener('click', createWorkflowTemplate);
    const newTemplateModal = document.getElementById('newTemplateModal');
    const templateForm = document.getElementById('templateForm');
    if (templateForm) {
        templateForm.addEventListener('submit', handleTemplateSubmit);
    }
    const submitBtn = document.getElementById('submitTemplate');
    if (submitBtn) {
        submitBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const form = document.getElementById('templateForm');
            if (form) {
                form.dispatchEvent(new Event('submit'));
            }
        });
    }

    const submitSpinner = document.getElementById('submitSpinner');

    // Global variables
    let editor = null;
    let tenantId = localStorage.getItem('tenantId');

    // Initialize
    if (tenantId) {
        showMainContent();
        loadWorkflowTemplates();
    }

    // Enable/disable proceed button based on input
    tenantIdInput.addEventListener('input', function () {
        proceedBtn.disabled = !this.value.trim();
    });

    // Set initial state of proceed button
    proceedBtn.disabled = !tenantIdInput.value.trim();

    // Handle tenant form submission
    tenantForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const inputTenantId = tenantIdInput.value.trim();
        if (inputTenantId) {
            localStorage.setItem('tenantId', inputTenantId);
            tenantId = inputTenantId;
            showMainContent();
            loadWorkflowTemplates();
        }
    });

    // Handle create first template button
    if (createFirstTemplateBtn) {
        createFirstTemplateBtn.addEventListener('click', function (e) {
            e.preventDefault();
            const modal = new bootstrap.Modal(newTemplateModal);
            modal.show();
        });
    }

    // Handle new template button (in header)
    if (newTemplateBtn) {
        newTemplateBtn.addEventListener('click', function (e) {
            e.preventDefault();
            const modal = new bootstrap.Modal(newTemplateModal);
            modal.show();
        });
    }

    // Initialize CodeMirror when modal is shown
    newTemplateModal.addEventListener('shown.bs.modal', function () {
        const editorElement = document.getElementById('editor');
        if (editorElement && !editor) {
            // Clear any existing content
            editorElement.innerHTML = '';

            // Create a new textarea for CodeMirror
            const textarea = document.createElement('textarea');
            textarea.id = 'templateEditor';
            editorElement.appendChild(textarea);

            // Initialize CodeMirror
            editor = CodeMirror.fromTextArea(textarea, {
                mode: 'htmlmixed',
                theme: 'monokai',
                lineNumbers: true,
                lineWrapping: true,
                autoCloseTags: true,
                matchTags: {
                    bothTags: true
                },
                extraKeys: {
                    "Ctrl-Space": "autocomplete"
                },
                viewportMargin: Infinity,
                autoRefresh: true
            });

            // Set initial value
            editor.setValue('<!-- Your HTML template here -->\n<!-- Use placeholders like {{workflowId}}, {{workflowName}}, {{workflowDescription}}, {{requesterServiceName}}, {{approveLink}}, {{rejectLink}} -->\n');

            // Sync CodeMirror with hidden textarea
            editor.on('change', (cm) => {
                document.getElementById('templateContent').value = cm.getValue();
            });

            // Force refresh the editor to ensure it's properly sized
            setTimeout(() => editor.refresh(), 0);
        } else if (editor) {
            editor.refresh();
        }
    });
    // Make sure both create buttons open the modal
    document.querySelectorAll('[id^="createFirstTemplateBtn"]').forEach(btn => {
        btn.addEventListener('click', createWorkflowTemplate);
    });

    // Load templates when page loads
    loadWorkflowTemplates();
    // Helper Functions

    function showLoading(isLoading) {
        if (isLoading) {
            proceedBtn.style.display = 'none';
            formLoader.style.display = 'block';
        } else {
            proceedBtn.style.display = 'block';
            formLoader.style.display = 'none';
        }
    }

    function showMainContent() {
        authModal.style.animation = 'fadeOut 0.3s ease-out forwards';
        setTimeout(() => {
            authModal.style.display = 'none';
            mainContent.style.display = 'block';
            document.body.style.overflow = 'auto';
        }, 300);
    }

    function loadWorkflowTemplates() {
        showLoading(true);

        fetch('/v1/workflow/templates', {
            headers: {
                'Content-Type': 'application/json',
                'tenant-id': tenantId
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to load templates');
            }
            return response.json();
        })
        .then(templates => {
            displayTemplates(templates);
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Failed to load templates. Please try again.');
        })
        .finally(() => {
            showLoading(false);
        });
    }

    function displayTemplates(templates) {
        const workflowList = document.getElementById('workflowList');
        const emptyState = document.getElementById('emptyState');

        if (!templates || templates.length === 0) {
            workflowList.innerHTML = '';
            emptyState.classList.remove('d-none');
            return;
        }

        emptyState.classList.add('d-none');

        workflowList.innerHTML = templates.map(template => `
            <tr>
                <td class="ps-4">
                    <div class="d-flex align-items-center">
                        <div class="ms-3">
                            <h6 class="mb-0">${template.name || 'Untitled Template'}</h6>
                            <small class="text-muted">${template.description || 'No description'}</small>
                        </div>
                    </div>
                </td>
                <td><code>${template.workflowId}</code></td>
                <td>
                    <span class="badge bg-primary">${template.channel || 'N/A'}</span>
                </td>
            </tr>
        `).join('');
    }

    function createWorkflowTemplate(e) {
        e.preventDefault();
        const modal = new bootstrap.Modal(document.getElementById('newTemplateModal'));
        modal.show();
    }

    // Add this function to your script.js
    async function handleTemplateSubmit(e) {
        e.preventDefault();
        // Get the form
        const form = e.target.tagName === 'FORM' ? e.target : document.getElementById('templateForm');
        if (!form) {
            console.error('Form not found');
            return;
        }

        const submitBtn = document.getElementById('submitTemplate') ||
                         form.querySelector('button[type="submit"]');
        if (!submitBtn) {
            console.error('Submit button not found');
            return;
        }


        const originalBtnText = submitBtn.innerHTML;

        try {
            // Show loading state
            submitBtn.disabled = true;
            const spinner = submitBtn.querySelector('.spinner-border');
            if (spinner) spinner.classList.remove('d-none');
            submitBtn.innerHTML = 'Creating... ' + spinner.outerHTML;

            // Get form values
            const templateData = {
                workflowId: document.getElementById('workflowId')?.value?.trim() || '',
                name: document.getElementById('workflowName')?.value?.trim() || '',
                description: document.getElementById('description')?.value?.trim() || '',
                channelType: document.querySelector('input[name="channelType"]:checked')?.value || 'EMAIL',
                htmlTemplate: editor?.getValue() || ''
            };

            // Validation
            if (!templateData.workflowId || !templateData.name) {
                throw new Error('Please fill in all required fields');
            }
            // Send request
            const response = await fetch('/v1/workflow/template', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'tenant-id': localStorage.getItem('tenantId') || ''
                },
                body: JSON.stringify(templateData)
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || 'Failed to create template');
            }

            // Close modal and refresh list
            const modal = bootstrap.Modal.getInstance(form.closest('.modal'));
            if (modal) modal.hide();

            form.reset();
            if (editor) editor.setValue(''); // Clear editor
            await loadWorkflowTemplates();

        } catch (error) {
            console.error('Error:', error);
            alert(error.message || 'Failed to create template');
        } finally {
            // Reset button state
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.innerHTML = originalBtnText;
            }
        }
    }

    const createBtn = document.getElementById('createFirstTemplateBtn');
        if (createBtn) {
            createBtn.addEventListener('click', createWorkflowTemplate);
        }
});